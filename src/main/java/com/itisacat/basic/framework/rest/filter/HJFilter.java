package com.itisacat.basic.framework.rest.filter;

import javax.servlet.Filter;
import java.util.Map;

public interface HJFilter extends Filter{
    
    /**
     * @Title: getOrder
     * @Description: 获取Filter过滤顺序问题，数值越小越先加载, 系统Filter从100开始
     * @return int
     */
    int getOrder();

    /**
     * @Title: getUrlPatterns
     * @Description: 获取Filter拦截通配符
     * @return String
     */
    String getUrlPatterns();
    
    /**
     * 
     * @Title: getInitParameter
     * @Description: Filter初始化参数 传入json格式如{"name":"hj"}
     * @return String    返回类型
     * @throws
     */
    default Map<String, String> getInitParameter(){
        return null;
    }

}
