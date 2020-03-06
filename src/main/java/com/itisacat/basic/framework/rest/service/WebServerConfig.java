package com.itisacat.basic.framework.rest.service;

import io.undertow.UndertowOptions;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

public class WebServerConfig {
    @Bean
    public UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {
        UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory();
        factory.addBuilderCustomizers(builder -> {
            builder.setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, true);
            builder.setServerOption(UndertowOptions.MAX_ENTITY_SIZE, 33554432l);// 默认32M
            builder.setServerOption(UndertowOptions.MULTIPART_MAX_ENTITY_SIZE, 33554432l); // 默认32M
        });
        return factory;
    }
}
