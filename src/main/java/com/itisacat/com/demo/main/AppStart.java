package com.itisacat.com.demo.main;

import com.itisacat.basic.framework.dao.DaoConfig;
import com.itisacat.basic.framework.rest.service.QQundertow;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@QQundertow
@Import(DaoConfig.class)
public class AppStart {

	public static void main(String[] args) {
		SpringApplication.run(AppStart.class, args);
		System.out.println("hello world");
	}

}
