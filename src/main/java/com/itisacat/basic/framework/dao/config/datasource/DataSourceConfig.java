package com.itisacat.basic.framework.dao.config.datasource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.itisacat.basic.framework.consts.PropConsts;
import com.itisacat.basic.framework.consts.SysDaoConsts;
import com.itisacat.basic.framework.consts.SysErrorConsts;
import com.itisacat.basic.framework.core.config.BaseProperties;
import com.itisacat.basic.framework.core.context.SpringApplicationContext;
import com.itisacat.basic.framework.core.exception.SysException;
import com.itisacat.basic.framework.core.util.SpecharsUtil;
import com.itisacat.basic.framework.dao.annotation.MyBatisRepository;
import com.itisacat.basic.framework.dao.config.mybatis.MybatisConfig;
import com.itisacat.basic.framework.dao.route.DataSourceSwitch;
import com.itisacat.basic.framework.dao.route.DynamicDataSource;
import lombok.Getter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataSourceConfig implements EnvironmentAware {

    private static final String SQL_SESSION_FACTORY = "sqlSessionFactory";
    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    @Getter
    private Map<Object, Object> targetDataSources = new LinkedHashMap<>();
    @Getter
    private DataSource defaultTargetDataSource;
    private RelaxedPropertyResolver propertyResolver;

    @Bean
    @Lazy
    @ConditionalOnMissingBean
    public DataSource dynamicDataSource() {
        DynamicDataSource ds = new DynamicDataSource();
        ds.setTargetDataSources(targetDataSources);
        ds.setDefaultTargetDataSource(defaultTargetDataSource);
        return ds;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(ApplicationContext context) {
        SpringApplicationContext springContext = new SpringApplicationContext();
        springContext.setApplicationContext(context);

        loadDataConfig(propertyResolver);

        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionTemplateBeanName(SysDaoConsts.SQL_SESSION_TEMPLATE);
        mapperScannerConfigurer.setAnnotationClass(MyBatisRepository.class);
        String basePackage = BaseProperties.getString(PropConsts.Dao.MYBATIS_BASEPACKAGE);
        if (basePackage == null || basePackage.isEmpty()) {
            throw new SysException(SysErrorConsts.SYS_ERROR_CODE,
                    String.format("mybatis.basePackage is null or empty,please check application-%s.properties", BaseProperties.getEnv()));
        }
        mapperScannerConfigurer.setBasePackage(basePackage);
        return mapperScannerConfigurer;
    }

    @Override
    public void setEnvironment(Environment env) {
        try {
            BaseProperties.loadData(env);
            // 读取配置文件获取更多数据源，也可以通过defaultDataSource读取数据库获取更多数据源
            propertyResolver = new RelaxedPropertyResolver(env, "jdbc.");
        } catch (Exception ex) {
           throw new SysException(SysErrorConsts.SYS_ERROR_CODE, ex.getMessage(), ex);
        }
    }

    private void loadDataConfig(RelaxedPropertyResolver propertyResolver) {
        try {
            String dsNames = propertyResolver.getProperty("names");
            Map<String, SqlSessionFactory> sqlSessionFactoryMap = new LinkedHashMap<>();
            if (dsNames == null) {
                try {
                    DruidDataSource druidDataSouce = buildDataSource(propertyResolver.getSubProperties(""));
                    druidDataSouce.init(); //初始化DB
                    defaultTargetDataSource = druidDataSouce;
                    MybatisConfig.registerTransaction("transactionManager", defaultTargetDataSource);
                    MybatisConfig.registerSqlSessionFactory(SQL_SESSION_FACTORY, defaultTargetDataSource);
                    sqlSessionFactoryMap.put("default", (SqlSessionFactory) SpringApplicationContext.getBean(SQL_SESSION_FACTORY));
                } catch (Exception e) {
                    throw new SysException(SysErrorConsts.SYS_ERROR_CODE, "Initialize dataSource failed! case:" + e.getMessage(), e);
                }
            } else {
                String[] ds = dsNames.split(",");
                for (int i = 0; i < ds.length; i++) {// 多个数据源
                    String dsPrefix = ds[i];
                    try {
                        Map<String, Object> dsParam = propertyResolver.getSubProperties(dsPrefix + ".");
                        DruidDataSource druidDataSouce = buildDataSource(dsParam);
                        druidDataSouce.init(); //初始化DB
                        DataSource dataSource = druidDataSouce;
                        if (i == 0) {
                            defaultTargetDataSource = dataSource;
                        }
                        DataSourceSwitch.addDataSource(dsPrefix);
                        targetDataSources.put(dsPrefix, dataSource);
                        MybatisConfig.registerTransaction(dsPrefix + BaseProperties.getProperty(PropConsts.Dao.TX_SUFFIX, "tx"), dataSource);

                        String sqlSessionFactoryKey = dsPrefix + SQL_SESSION_FACTORY;
                        MybatisConfig.registerSqlSessionFactory(sqlSessionFactoryKey, dataSource);
                        sqlSessionFactoryMap.put(dsPrefix, (SqlSessionFactory) SpringApplicationContext.getBean(sqlSessionFactoryKey));
                    } catch (Exception e) {
                        throw new SysException(SysErrorConsts.SYS_ERROR_CODE, String.format("Initialize dsName:[%s] failed! case:%s", dsPrefix, e.getMessage()), e);
                    }
                }
            }
            MybatisConfig.registerSqlSessionTemplate(sqlSessionFactoryMap);
        } catch (Exception ex) {
            throw new SysException(SysErrorConsts.SYS_ERROR_CODE, ex.getMessage(), ex);
        }
    }


    private DruidDataSource buildDataSource(Map<String, Object> ds) {
        try (DruidDataSource druidDataSource = new DecryptDruidSource()) {
            String driverClassName = (String) ds.get("driverClassName");
            druidDataSource.setDriverClassName(driverClassName);
            druidDataSource.setUrl((String) ds.get("url"));
            druidDataSource.setUsername((String) ds.get("username"));
            druidDataSource.setPassword((String) ds.get("password"));

            String initialSize = (String) ds.get("initialSize");
            if (initialSize != null) {
                druidDataSource.setInitialSize(Integer.valueOf(initialSize));
            }

            String connectionProperties = (String) ds.get("connectionProperties");
            if (connectionProperties != null) {
                druidDataSource.setConnectionProperties(connectionProperties);
            }

            String maxActive = (String) ds.get("maxActive");
            if (maxActive != null) {
                druidDataSource.setMaxActive(Integer.valueOf(maxActive));
            }

            String minIdle = (String) ds.get("minIdle");
            if (minIdle != null) {
                druidDataSource.setMinIdle(Integer.valueOf(minIdle));
            }

            String maxWait = (String) ds.get("maxWait");
            if (maxWait != null) {
                druidDataSource.setMaxWait(Long.valueOf(maxWait));
            }

            String timeBetweenEvictionRunsMillis = (String) ds.get("timeBetweenEvictionRunsMillis");
            if (timeBetweenEvictionRunsMillis != null) {
                druidDataSource.setTimeBetweenEvictionRunsMillis(Long.valueOf(timeBetweenEvictionRunsMillis));
            }

            String connectionInitSqls = (String) ds.get("connectionInitSqls");
            if (connectionInitSqls != null) {
                List<String> initSqls = Splitter.on(SpecharsUtil.SYMBOL_SEMICOLON).trimResults().omitEmptyStrings().splitToList(connectionInitSqls);
                druidDataSource.setConnectionInitSqls(initSqls);
            }

            String minEvictableIdleTimeMillis = (String) ds.get("minEvictableIdleTimeMillis");
            if (minEvictableIdleTimeMillis != null) {
                druidDataSource.setMinEvictableIdleTimeMillis(Long.valueOf(minEvictableIdleTimeMillis));
            }

            String maxEvictableIdleTimeMillis = (String) ds.get("maxEvictableIdleTimeMillis");
            if (maxEvictableIdleTimeMillis != null) {
                druidDataSource.setMaxEvictableIdleTimeMillis(Long.valueOf(maxEvictableIdleTimeMillis));
            }

            druidDataSource.setTestWhileIdle(BaseProperties.getProperty(PropConsts.Dao.JDBC_TESTWHILEIDLE, Boolean.class, true));
            druidDataSource.setTestOnBorrow(BaseProperties.getProperty(PropConsts.Dao.JDBC_TESTONBORROW, Boolean.class, false));
            druidDataSource.setTestOnReturn(BaseProperties.getProperty(PropConsts.Dao.JDBC_TESTONRETURN, Boolean.class, false));

            if (!MYSQL_DRIVER.equals(driverClassName)) {
                druidDataSource.setPoolPreparedStatements(true);
                String maxPoolPreparedStatementPerConnectionSize = (String) ds.get("maxPoolPreparedStatementPerConnectionSize");
                if (maxPoolPreparedStatementPerConnectionSize != null) {
                    druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.valueOf(maxPoolPreparedStatementPerConnectionSize));
                }
            }

            druidDataSource.setValidationQuery("select 1");
            druidDataSource.setValidationQueryTimeout(BaseProperties.getProperty(PropConsts.Dao.JDBC_VALIDATIONQUERYTIMEOUT, Integer.class, 5));
            //超过时间限制是否回收
            druidDataSource.setRemoveAbandoned(BaseProperties.getProperty(PropConsts.Dao.JDBC_REMOVEABANDONED, Boolean.class, true));
            //超时时间；单位为秒。30秒
            druidDataSource.setRemoveAbandonedTimeout(BaseProperties.getProperty(PropConsts.Dao.JDBC_REMOVEABANDONEDTIMEOUT, Integer.class, 30));
            //关闭abanded连接时输出错误日志
            druidDataSource.setLogAbandoned(BaseProperties.getProperty(PropConsts.Dao.JDBC_LOGABANDONED, Boolean.class, true));

            List<Filter> filters = Lists.newArrayList(statFilter());

            if (BaseProperties.getProperty(PropConsts.Dao.JDBC_CHECK_SQL, Boolean.class, true)) {
               // filters.add(wallFilter());
            }
            druidDataSource.setProxyFilters(filters);

            return druidDataSource;
        } catch (Exception ex) {
            throw new SysException(SysErrorConsts.SYS_ERROR_CODE, ex.getMessage(), ex);
        }

    }


    private static WallFilter wallFilter() {
        WallFilter wallFilter = new WallFilter();
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);
        wallConfig.setSelectWhereAlwayTrueCheck(false);
        wallConfig.setSelectHavingAlwayTrueCheck(false);
        wallConfig.setSelectUnionCheck(BaseProperties.getProperty(PropConsts.Dao.DRUID_SELECT_UNIONCHECK, Boolean.class, true));
        wallFilter.setConfig(wallConfig);
        wallFilter.initProviders();
        return wallFilter;
    }

    private static StatFilter statFilter() {
        StatFilter statFilter = new StatFilter();
        statFilter.setSlowSqlMillis(BaseProperties.getProperty(PropConsts.Dao.DRUID_SHOW_TIMEOUT, Long.class, 3000l));
        statFilter.setLogSlowSql(BaseProperties.getProperty(PropConsts.Dao.DRUID_SHOW_SQL, Boolean.class, true));
        statFilter.setMergeSql(BaseProperties.getProperty(PropConsts.Dao.DRUID_MERGE_SQL, Boolean.class, false));
        return statFilter;
    }

}
