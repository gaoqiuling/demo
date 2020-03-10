package com.itisacat.basic.framework.rest.config.monitor;


import com.itisacat.basic.framework.rest.filter.HJFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
//@Configuration ??不需要注入啊，这是为啥呢
public class FilterConfig implements ApplicationContextAware, BeanFactoryPostProcessor {
    private static final String FILTER_BEAN_SUFFIX = "RegistrationBean";

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Map<String, HJFilter> filterBeans = beanFactory.getBeansOfType(HJFilter.class);
       
        for(Map.Entry<String, HJFilter> entry : filterBeans.entrySet()){
            String name = entry.getKey();
            HJFilter filter = entry.getValue();
            BeanDefinitionBuilder beanDefinitionBuilder =
                    BeanDefinitionBuilder.genericBeanDefinition(FilterRegistrationBean.class);
            beanDefinitionBuilder.addPropertyValue("filter", filter);
            beanDefinitionBuilder.addPropertyValue("urlPatterns", filter.getUrlPatterns());
            
            Map<String, String> initParameter = filter.getInitParameter();
            if(initParameter != null){
                beanDefinitionBuilder.addPropertyValue("initParameters", initParameter);
            }
            
            beanDefinitionBuilder.addPropertyValue("order", filter.getOrder());
            log.info("filterName:" + name + ",order:" + filter.getOrder());
            
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
            defaultListableBeanFactory.registerBeanDefinition(name + FILTER_BEAN_SUFFIX, beanDefinitionBuilder.getRawBeanDefinition());
        }
        
        
        
    }


}
