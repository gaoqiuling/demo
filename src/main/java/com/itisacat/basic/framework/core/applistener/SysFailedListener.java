package com.itisacat.basic.framework.core.applistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.TimeUnit;

@Slf4j
public class SysFailedListener implements ApplicationListener<ApplicationFailedEvent>{

	@Override
	public void onApplicationEvent(ApplicationFailedEvent event) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(1);
					log.info("System sleep exit after 1s!");
					System.exit(0);
				} catch (InterruptedException e) {
					log.warn(e.getMessage(), e);
					Thread.currentThread().interrupt();
				}
			}
		}).start();
	}

}
