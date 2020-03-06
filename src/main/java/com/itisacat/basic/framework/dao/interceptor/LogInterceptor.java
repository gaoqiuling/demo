package com.itisacat.basic.framework.dao.interceptor;

import com.google.common.base.Stopwatch;
import com.itisacat.basic.framework.consts.SysDaoConsts;
import com.itisacat.basic.framework.dao.route.DataSourceSwitch;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
@Slf4j
public class LogInterceptor implements Interceptor {


    private Boolean isShowSql;
    private Integer sqlTimout;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Stopwatch sp = Stopwatch.createStarted();

        Object obj = invocation.proceed();

        long useTime = sp.stop().elapsed(TimeUnit.MILLISECONDS);
        if ((isShowSql != null && isShowSql) && (sqlTimout != null && useTime > sqlTimout)) {
            final Object[] args = invocation.getArgs();
            MappedStatement mappedStatement = (MappedStatement) args[0];
            String sqlId = mappedStatement.getId();
            Object parameterObject = args[1];
            BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
            String sqlStr = String.valueOf(boundSql.getSql().replaceAll("\\s+", " ").replace("\n", ""));
            log.info("hjframeworkSlowLogDB show SQL:dataSource:{}, sqlId:{}, cost time:{}ms, sql:{}",
                    DataSourceSwitch.getDataSource() == null ? "default" : DataSourceSwitch.getDataSource(), sqlId,
                    String.valueOf(useTime), sqlStr);
        }
        return obj;
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
        isShowSql = Boolean.valueOf(properties.getProperty(SysDaoConsts.SHOW_SQL));
        sqlTimout = Integer.valueOf(properties.getProperty(SysDaoConsts.SHOW_TIMEOUT));

    }
}
