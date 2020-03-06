package com.itisacat.basic.framework.dao.interceptor;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Dialect;
import com.github.pagehelper.PageException;
import com.github.pagehelper.cache.Cache;
import com.github.pagehelper.cache.CacheFactory;
import com.github.pagehelper.util.MSUtils;
import com.github.pagehelper.util.StringUtil;
import com.google.common.base.Stopwatch;
import com.itisacat.basic.framework.consts.SysDaoConsts;
import com.itisacat.basic.framework.consts.SysErrorConsts;
import com.itisacat.basic.framework.core.exception.SysException;
import com.itisacat.basic.framework.dao.route.DataSourceSwitch;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Mybatis - 通用分页拦截器<br/>
 *
 * @author huangxin
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Intercepts(@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
		RowBounds.class, ResultHandler.class }))
@Slf4j
public class PageInterceptor implements Interceptor {
	// 缓存count查询的ms
	protected Cache<CacheKey, MappedStatement> msCountMap = null;
	private Dialect dialect;
	private String default_dialect_class = "com.itisacat.basic.framework.dao.pagehelper.PageHelper";
	private Field additionalParametersField;

	private static final String DEFAULT_EMPTY_VALAUE = "{},[],{[]},[{}]";

	private static final String SQL_KEYWORD_LIMIT = "LIMIT";

	private Boolean isShowSql;
	private Integer sqlTimout;
	private Integer maxLimit;
	private Boolean maxLimitEnable;

	@Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            // 获取拦截方法的参数
            Stopwatch sp = Stopwatch.createStarted();
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            Object parameterObject = args[1];
            RowBounds rowBounds = (RowBounds) args[2];
            args[2] = RowBounds.DEFAULT;
            ResultHandler resultHandler = (ResultHandler) args[3];
            List resultList;
            String pageSql = null;
            String countSql = null;

            if(rowBounds.getLimit() == 0) {
            //	throw new SysException("RowBound limit can't 0!");
            }
            Executor executor = (Executor) invocation.getTarget();
            BoundSql boundSql = ms.getBoundSql(parameterObject);
            // 反射获取动态参数
            Map<String, Object> additionalParameters = (Map<String, Object>) additionalParametersField.get(boundSql);

            // 调用方法判断是否需要进行分页，如果不需要，直接返回结果
            if (!dialect.skip(ms, parameterObject, rowBounds)) {
                // 判断是否需要进行 count 查询
                if (dialect.beforeCount(ms, parameterObject, rowBounds)) {
                    // 创建 count 查询的缓存 key
                    CacheKey countKey = executor.createCacheKey(ms, parameterObject, RowBounds.DEFAULT, boundSql);
                    countKey.update("_Count");
                    MappedStatement countMs = msCountMap.get(countKey);
                    if (countMs == null) {
                        // 根据当前的 ms 创建一个返回值为 Long 类型的 ms
                        countMs = MSUtils.newCountMappedStatement(ms);
                        msCountMap.put(countKey, countMs);
                    }
                    // 调用方言获取 count sql
                    countSql = dialect.getCountSql(ms, boundSql, parameterObject, rowBounds, countKey);
                    BoundSql countBoundSql = new BoundSql(ms.getConfiguration(), countSql,
                            boundSql.getParameterMappings(), parameterObject);
                    // 当使用动态 SQL 时，可能会产生临时的参数，这些参数需要手动设置到新的 BoundSql 中
                    for (Map.Entry<String, Object> entry : additionalParameters.entrySet()) {
                        countBoundSql.setAdditionalParameter(entry.getKey(), additionalParameters.get(entry.getKey()));
                    }
                    // 执行 count 查询
                    Object countResultList = executor.query(countMs, parameterObject, RowBounds.DEFAULT, resultHandler,
                            countKey, countBoundSql);
                    Long count = (Long) ((List) countResultList).get(0);
                    // 处理查询总数
                    // 返回 true 时继续分页查询，false 时直接返回
                    if (!dialect.afterCount(count, parameterObject, rowBounds)) {
                        // 当查询总数为 0 时，直接返回空的结果
                        return dialect.afterPage(new ArrayList(), parameterObject, rowBounds);
                    }
                }
                // 判断是否需要进行分页查询
                if (dialect.beforePage(ms, parameterObject, rowBounds)) {
                    // 生成分页的缓存 key
                    CacheKey pageKey = executor.createCacheKey(ms, parameterObject, rowBounds, boundSql);
                    // 处理参数对象
                    parameterObject = dialect.processParameterObject(ms, parameterObject, boundSql, pageKey);
                    // 调用方言获取分页 sql
                    pageSql = dialect.getPageSql(ms, boundSql, parameterObject, rowBounds, pageKey);
                    BoundSql pageBoundSql = new BoundSql(ms.getConfiguration(), pageSql,
                            boundSql.getParameterMappings(), parameterObject);
                    // 设置动态参数
                    for (Map.Entry<String, Object> entry : additionalParameters.entrySet()) {
                        pageBoundSql.setAdditionalParameter(entry.getKey(), additionalParameters.get(entry.getKey()));
                    }
                    // 执行分页查询
                    resultList = executor.query(ms, parameterObject, RowBounds.DEFAULT, resultHandler, pageKey,
                            pageBoundSql);
                } else {
                    resultList = queryByMaxLimit(invocation, executor, ms, parameterObject, boundSql,
                            additionalParameters, resultHandler);
                }
            } else {
                resultList = queryByMaxLimit(invocation, executor, ms, parameterObject, boundSql, additionalParameters,
                        resultHandler);
            }
            long useTime = sp.stop().elapsed(TimeUnit.MILLISECONDS);
            if ((isShowSql != null && isShowSql) && (sqlTimout != null && useTime > sqlTimout)) {
            	String sqlStr = boundSql.getSql();
                if (pageSql != null) {
                	sqlStr = pageSql;
                	
                }
                sqlStr = String.valueOf(sqlStr.replaceAll("\\s+", " ").replace("\n", ""));
                log.info("hjframeworkSlowLogDB PageSql show SQL:dataSource:{}, sqlId:{}, count:{}, cost time:{}ms, sql:{}",
                            DataSourceSwitch.getDataSource() == null ? "default" : DataSourceSwitch.getDataSource(),
                            ms.getId(), resultList.size(), String.valueOf(useTime), sqlStr);
                
                if (countSql != null) {
                    sqlStr = String.valueOf(countSql.replaceAll("\\s+", " ").replace("\n", ""));
                    log.info("hjframeworkSlowLogDB CountSql show SQL:dataSource:{}, sqlId:{}, count:{}, cost time:{}ms, sql:{}",
                            DataSourceSwitch.getDataSource() == null ? "default" : DataSourceSwitch.getDataSource(),
                            ms.getId(), resultList.size(), String.valueOf(useTime), sqlStr);
                }
            }

            return dialect.afterPage(resultList, parameterObject, rowBounds);
        }finally

	{
		dialect.afterAll();
	}

	}

	private List queryByMaxLimit(Invocation invocation, Executor executor, MappedStatement ms, Object parameterObject,
                                 BoundSql boundSql, Map<String, Object> additionalParameters, ResultHandler resultHandler) {

		try {
			if (!maxLimitEnable) {
				return (List) invocation.proceed();
			}

			if (!boundSql.getSql().toUpperCase().contains(SQL_KEYWORD_LIMIT)) {
				if (parameterObject == null || DEFAULT_EMPTY_VALAUE.contains(JSON.toJSONString(parameterObject))) {
					log.warn("query parameter is empty! sql_id:{}", ms.getId());

					RowBounds rowBounds = new RowBounds(0, maxLimit);
					if (!dialect.skip(ms, parameterObject, rowBounds)) {
						CacheKey pageKey = executor.createCacheKey(ms, parameterObject, rowBounds, boundSql);

						// 调用方言获取分页 sql
						String pageSql = dialect.getPageSql(ms, boundSql, parameterObject, rowBounds, pageKey);
						BoundSql pageBoundSql = new BoundSql(ms.getConfiguration(), pageSql,
								boundSql.getParameterMappings(), parameterObject);
						// 设置动态参数
						for (Map.Entry<String, Object> entry : additionalParameters.entrySet()) {
							pageBoundSql.setAdditionalParameter(entry.getKey(),
									additionalParameters.get(entry.getKey()));
						}
						// 执行分页查询
						return executor.query(ms, parameterObject, RowBounds.DEFAULT, resultHandler, pageKey,
								pageBoundSql);
					} else {
						return (List) invocation.proceed();
					}
				} else {
					return (List) invocation.proceed();
				}
			} else {
				return (List) invocation.proceed();
			}
		} catch (Exception e) {
			throw new SysException(SysErrorConsts.SYS_ERROR_CODE, e.getMessage(), e);
		}

	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		// 缓存 count ms
		msCountMap = CacheFactory.createCache(properties.getProperty("msCountCache"), "ms", properties);
		String dialectClass = properties.getProperty("dialect");
		if (StringUtil.isEmpty(dialectClass)) {
			dialectClass = default_dialect_class;
		}
		try {
			Class<?> aClass = Class.forName(dialectClass);
			dialect = (Dialect) aClass.newInstance();

			isShowSql = Boolean.valueOf(properties.getProperty(SysDaoConsts.SHOW_SQL));
			sqlTimout = Integer.valueOf(properties.getProperty(SysDaoConsts.SHOW_TIMEOUT));
			maxLimit = Integer.valueOf(properties.getProperty(SysDaoConsts.MAX_LIMIT));
			maxLimitEnable = Boolean.valueOf(properties.getProperty(SysDaoConsts.MAXLIMIT_ENABLE));
		} catch (Exception e) {
			throw new PageException(e);
		}
		dialect.setProperties(properties);
		try {
			// 反射获取 BoundSql 中的 additionalParameters 属性
			additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
			additionalParametersField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new PageException(e);
		}
	}

}
