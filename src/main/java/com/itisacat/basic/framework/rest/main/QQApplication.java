package com.itisacat.basic.framework.rest.main;

import com.itisacat.basic.framework.consts.GeneralConsts;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;

import java.util.ServiceLoader;

public class QQApplication {

    private QQApplication() {
    }

    public static void start(Class<?> startClass, String[] args) {
        System.setProperty("file.encoding", GeneralConsts.DEFAULT_CHARSET_NAME);
        System.setProperty("sun.jnu.encoding", GeneralConsts.DEFAULT_CHARSET_NAME);
        System.setProperty("sun.zip.encoding", GeneralConsts.DEFAULT_CHARSET_NAME);
        ResourceBanner rb = new ResourceBanner(new ClassPathResource("banner.txt"));
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        ServiceLoader.load(ApplicationListener.class).forEach(listenter -> builder.application().addListeners(listenter));
        builder.sources(startClass).banner(rb).run(args);
    }

}
