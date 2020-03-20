package com.itisacat.basic.framework.core.applistener;

import com.itisacat.basic.framework.core.config.BaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;


@Slf4j
@Configuration
@Component
public class PropertiesListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment ce = event.getEnvironment();
        log.info("Load config!");
        //HjConfig.remoteLoadConfig(ce);
        log.info("BaseProperties load data by env!");
        BaseProperties.loadData(ce);
    }
    
    
    
    


}
