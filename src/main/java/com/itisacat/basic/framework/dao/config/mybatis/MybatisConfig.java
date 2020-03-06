package com.itisacat.basic.framework.dao.config.mybatis;

import com.itisacat.basic.framework.consts.PropConsts;
import com.itisacat.basic.framework.consts.SysDaoConsts;
import com.itisacat.basic.framework.core.config.BaseProperties;
import com.itisacat.basic.framework.core.context.SpringApplicationContext;
import com.itisacat.basic.framework.dao.interceptor.DbTypeInterceptor;
import com.itisacat.basic.framework.dao.interceptor.LogInterceptor;
import com.itisacat.basic.framework.dao.interceptor.PageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class MybatisConfig {
    private MybatisConfig(){}
    
    private static final String IS_TRUE = "true";
    private static final String IS_FALSE = "false";

    public static void registerSqlSessionFactory(String name, DataSource dataSource) {
        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.rootBeanDefinition(SqlSessionFactoryBean.class.getName());
        beanDefinitionBuilder.addPropertyValue("dataSource", dataSource);
        beanDefinitionBuilder.setInitMethodName("getObject");
        beanDefinitionBuilder.addPropertyValue("typeAliasesPackage",
                BaseProperties.getString(PropConsts.Dao.MYBATIS_MODEL));
        beanDefinitionBuilder.addPropertyValue("configuration", getConfiguration());
        beanDefinitionBuilder.addPropertyValue("typeHandlersPackage",
                BaseProperties.getString("mybatis.typeHandlersPackage"));
        VFS.addImplClass(SpringBootVFS.class);

        Map<String, Interceptor> interceptor = SpringApplicationContext.getBeans(Interceptor.class);// 获取用户自定义的Interceptor
        int size = interceptor.size();
        Interceptor[] interceptors = new Interceptor[size + 3];

        // 先加载用户自定Interceptor
        int j = 0;
        for (Map.Entry<String, Interceptor> entry : interceptor.entrySet()) {
            interceptors[j] = entry.getValue();
            j++;
        }

        LogInterceptor logInterceptor = new LogInterceptor();
        Properties logproperties = new Properties();

        String showTimeout = BaseProperties.getProperty(PropConsts.Dao.MYBATIS_SHOW_TIMEOUT, "3000");
        String showSql = BaseProperties.getProperty(PropConsts.Dao.MYBATIS_SHOW_SQL, IS_FALSE);
        String maxLimit = BaseProperties.getProperty(PropConsts.Dao.MYBATIS_MAXLIMIT_ENABLE, IS_FALSE);

        logproperties.setProperty(SysDaoConsts.SHOW_TIMEOUT, showTimeout);
        logproperties.setProperty(SysDaoConsts.SHOW_SQL, showSql);
        logInterceptor.setProperties(logproperties);
        interceptors[size] = logInterceptor;
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties pageProperties = new Properties();
        pageProperties.setProperty(SysDaoConsts.SHOW_TIMEOUT, showTimeout);
        pageProperties.setProperty(SysDaoConsts.SHOW_SQL, showSql);
        pageProperties.setProperty(SysDaoConsts.MAXLIMIT_ENABLE, maxLimit);
        pageProperties.setProperty(SysDaoConsts.MAX_LIMIT,
                BaseProperties.getProperty(PropConsts.Dao.MYBATIS_MAX_LIMIT, "500"));// 查询未加入条件，默认设置分页最大500条
        pageProperties.setProperty("offsetAsPageNum",
                BaseProperties.getProperty(PropConsts.Dao.MYBATIS_PAGE_OFFSETASPAGENUM, IS_TRUE));
        pageProperties.setProperty("rowBoundsWithCount",
                BaseProperties.getProperty(PropConsts.Dao.MYBATIS_PAGE_ROWBOUNDSWITHCOUNT, IS_TRUE));
        pageProperties.setProperty("pageSizeZero", BaseProperties.getProperty(PropConsts.Dao.MYBATIS_PAGE_PAGESIZEZERO, IS_TRUE));
        pageProperties.setProperty("reasonable", BaseProperties.getProperty(PropConsts.Dao.MYBATIS_PAGE_REASONABLE, IS_FALSE));
        pageProperties.setProperty("params", BaseProperties.getProperty(PropConsts.Dao.MYBATIS_PAGE_PARAMS, 
                "pageNum=pageHelperStart;pageSize=pageHelperRows;"));
        pageProperties.setProperty("supportMethodsArguments",
                BaseProperties.getProperty(PropConsts.Dao.MYBATIS_PAGE_SUPPORTMETHODSARGUMENTS, IS_FALSE));
        pageProperties.setProperty("returnPageInfo", BaseProperties.getProperty(PropConsts.Dao.MYBATIS_PAGE_RETURNPAGEINFO, "none"));
        pageInterceptor.setProperties(pageProperties);
        interceptors[size + 1] = pageInterceptor;
        interceptors[size + 2] = new DbTypeInterceptor();
        beanDefinitionBuilder.addPropertyValue("plugins", interceptors);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            beanDefinitionBuilder.addPropertyValue("mapperLocations",
                    resolver.getResources("classpath*:mapper/**/*.xml"));
        } catch (IOException e) {
          //  throw new SysException(e);
        }
        beanDefinitionBuilder.setLazyInit(true);
        SpringApplicationContext.register(name, beanDefinitionBuilder.getRawBeanDefinition());
    }

    public static void registerTransaction(String name, DataSource source) {
        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
        beanDefinitionBuilder.addPropertyValue("dataSource", source);
        beanDefinitionBuilder.setLazyInit(true);
        SpringApplicationContext.register(name, beanDefinitionBuilder.getRawBeanDefinition());
    }

    public static void registerSqlSessionTemplate(Map<String, SqlSessionFactory> sqlSessionFactorys) {
        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.genericBeanDefinition(CustomSqlSessionTemplate.class);
        beanDefinitionBuilder.addConstructorArgValue(sqlSessionFactorys.entrySet().iterator().next().getValue());
        beanDefinitionBuilder.addPropertyValue("targetSqlSessionFactorys", sqlSessionFactorys);
        beanDefinitionBuilder.setLazyInit(true);
        SpringApplicationContext.register(SysDaoConsts.SQL_SESSION_TEMPLATE, beanDefinitionBuilder.getRawBeanDefinition());
    }


    private static org.apache.ibatis.session.Configuration getConfiguration() {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setLogImpl(Log4j2Impl.class);
        //执行SQL超时设置 默认5分钟
        configuration.setDefaultStatementTimeout(300);
        try {
            Map<String, ConfigurationCustomizer> configurationCustomizers =
                    SpringApplicationContext.getBeans(ConfigurationCustomizer.class);
            configurationCustomizers
                    .forEach((key, configurationCustomizer) -> configurationCustomizer.customize(configuration));
        } catch (Exception e) {
            log.warn("ConfigurationCustomizer impls is not found!", e);
        }
        return configuration;
    }

}
