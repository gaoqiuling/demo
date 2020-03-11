package com.itisacat.com.demo.main;

import com.itisacat.basic.framework.core.config.BaseProperties;
import com.itisacat.basic.framework.core.util.IpUtil;
import com.itisacat.basic.framework.dao.DaoConfig;
import com.itisacat.basic.framework.rest.config.RestConfig;
import com.itisacat.basic.framework.rest.service.QQundertow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"com.itisacat.com"})
@QQundertow
@Import({DaoConfig.class, RestConfig.class})
@Slf4j
public class AppStart {
    public static void main(String[] args) {
        SpringApplication.run(AppStart.class, args);
        String message = String.format("%s(%s)站点正常启动", BaseProperties.getProperty("default.application.name", ""), IpUtil.getIp());
        log.info(message);
    }

}
