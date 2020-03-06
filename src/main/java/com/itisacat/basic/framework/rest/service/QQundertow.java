package com.itisacat.basic.framework.rest.service;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Import(WebServerConfig.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface QQundertow {

}