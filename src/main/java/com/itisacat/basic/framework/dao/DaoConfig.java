package com.itisacat.basic.framework.dao;


import com.itisacat.basic.framework.dao.config.datasource.DataSourceConfig;
import org.springframework.context.annotation.Import;


@Import({DataSourceConfig.class})
public class DaoConfig {
}
