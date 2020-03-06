package com.itisacat.basic.framework.dao.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;


/**
 * 以注解方式来标识MyBatis的DAO
 * 
 * @author caobin
 * @version 1.0 2015年9月25日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
@Inherited
public @interface MyBatisRepository {

}
