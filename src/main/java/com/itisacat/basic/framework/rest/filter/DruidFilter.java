package com.itisacat.basic.framework.rest.filter;

import com.alibaba.druid.support.http.WebStatFilter;
import com.itisacat.basic.framework.consts.PropConsts;
import com.itisacat.basic.framework.consts.SysRestConsts;
import com.itisacat.basic.framework.core.config.BaseProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class DruidFilter extends WebStatFilter implements HJFilter{

    @Override
    public int getOrder() {
        return BaseProperties.getProperty(PropConsts.Rest.FILTER_ORDER_DRUID, Integer.class, 101);
    }

    @Override
    public String getUrlPatterns() {
       return SysRestConsts.INCLUDE_URL_PATTEER;
    }

    @Override
    public Map<String, String> getInitParameter() {
        Map<String, String> map = new HashMap<>(1);
        map.put("exclusions", BaseProperties.getProperty(PropConsts.Rest.REST_REQUESTILTER_EXCLUSIONS_URL, SysRestConsts.EXCLUSIONS_URL_PATTEER));
        return map;
    }

}
