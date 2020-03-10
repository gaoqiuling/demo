package com.itisacat.basic.framework.rest.config.mvc;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.google.common.collect.Lists;

import java.util.List;

public interface IConvertersHandler {

    List<SerializeConfig> serializeConfigs = Lists.newArrayList();

    void process(FastJsonConfig fastJsonConfig);

    /**
	 * 注册SerializeConfig，用于手动序列化
	 * 
	 * @author gerry 
	 * @param serializeConfig
	 */
	void register(SerializeConfig serializeConfig);

}
