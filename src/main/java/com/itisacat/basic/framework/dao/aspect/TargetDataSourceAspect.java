package com.itisacat.basic.framework.dao.aspect;


import com.itisacat.basic.framework.dao.annotation.TargetDataSource;
import com.itisacat.basic.framework.dao.route.DataSourceSwitch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 切换数据源Advice
 */
@Aspect
@Component
@Slf4j
public class TargetDataSourceAspect {

    @Before(" @annotation(ds)")
    public void changeDataSource(JoinPoint point, TargetDataSource ds) {
        String dsId = ds.value();
        if (!DataSourceSwitch.containsDataSource(dsId)) {
            log.error("数据源[{}]不存在，使用默认数据源 > {}", ds.value(), point.getSignature());
        } else {
            log.debug("Use DataSource : {} > {}", ds.value(), point.getSignature());
            DataSourceSwitch.setDataSource(ds.value());
        }
    }

    @After(" @annotation(ds)")
    public void restoreDataSource(JoinPoint point, TargetDataSource ds) {
        log.debug("Revert DataSource : {} > {}", ds.value(), point.getSignature());
        DataSourceSwitch.clearDataSource();
    }

}