package com.itisacat.com.demo.main;

import com.itisacat.basic.framework.dao.DaoConfig;
import com.itisacat.basic.framework.rest.config.RestConfig;
import com.itisacat.basic.framework.rest.service.QQundertow;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"com.itisacat.com"})
@QQundertow
@Import({DaoConfig.class, RestConfig.class})
public class AppStart {
    public static void main(String[] args) {
        SpringApplication.run(AppStart.class, args);
    }

}
