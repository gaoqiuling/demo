package com.itisacat.basic.framework.core.applistener;

import com.itisacat.basic.framework.core.context.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringContextListener implements ApplicationListener<ApplicationPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        if(SpringApplicationContext.getApplicationContext() == null){
            SpringApplicationContext context = new SpringApplicationContext();
            context.setApplicationContext(event.getApplicationContext());
            log.info("SpringContext load success!");
        }
    }
}