package com.itisacat.com.demo.main;

import com.itisacat.basic.framework.dao.route.DataSourceSwitch;
import com.itisacat.com.demo.main.com.demo.dao.DakaDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@PropertySource("classpath:application.properties")
@ComponentScan("com.itisacat.basic.framework")
public class DemoApplicationTests {
    @Autowired
    private DakaDao dakaDao;

    @Test
    public void getDakaName() {
        DataSourceSwitch.setDataSource("daka_slave");
        String name = dakaDao.getDakaName(2);
        System.out.println(name);
    }

}
