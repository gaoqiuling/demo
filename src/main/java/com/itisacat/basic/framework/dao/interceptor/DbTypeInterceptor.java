package com.itisacat.basic.framework.dao.interceptor;

import com.alibaba.druid.util.JdbcUtils;
import com.itisacat.basic.framework.dao.route.DataSourceSwitch;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;

import java.sql.Connection;
import java.util.Properties;

/**
 * 获取dbtype拦截器
 * 
 * @author huangxin
 *
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DbTypeInterceptor implements Interceptor {

    private static final String DBTYPE_SQLSERVER = "Microsoft SQL Server";

    public Object intercept(Invocation invocation) throws Throwable {
        String dbType = ((Connection) invocation.getArgs()[0]).getMetaData().getDatabaseProductName();
        if (DBTYPE_SQLSERVER.equals(dbType)) {
            DataSourceSwitch.setDataType(JdbcUtils.SQL_SERVER);
        } else {
            DataSourceSwitch.setDataType(JdbcUtils.MYSQL);
        }

        return invocation.proceed();
    }

    /**
     * 拦截器对应的封装原始对象的方法
     */
    public Object plugin(Object arg0) {
        if (arg0 instanceof StatementHandler) {
            return Plugin.wrap(arg0, this);
        } else {
            return arg0;
        }
    }

    /**
     * 设置注册拦截器时设定的属性
     */
    public void setProperties(Properties p) {

    }

}
