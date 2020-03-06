package com.itisacat.basic.framework.dao.config.mybatis;

import org.apache.ibatis.session.Configuration;

/**
 *
* @ClassName: ConfigurationCustomizer
* @Description: 处理mybatis 配置信息处理接口
*
 */
public interface ConfigurationCustomizer {

  void customize(Configuration configuration);

}
