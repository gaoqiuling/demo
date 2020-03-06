package com.itisacat.basic.framework.core.context;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

/**
 * spring bean 的所有bean存储
 */
public class SpringApplicationContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringApplicationContext.applicationContext = applicationContext;
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }

    public static <T> Map<String, T> getBeans(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }

    public static void register(String beanName, AbstractBeanDefinition abstractBeanDefinition) {
        Preconditions.checkNotNull(applicationContext, "Spring ApplicationContext is null!");
        
        ConfigurableApplicationContext configurableApplicationContext =
                (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory defaultListableBeanFactory =
                (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        defaultListableBeanFactory.registerBeanDefinition(beanName, abstractBeanDefinition);
    }

    public static void removeBean(String beanId) {
        if (beanId == null || beanId.isEmpty()) {
            return;
        }
        Preconditions.checkNotNull(applicationContext, "Spring ApplicationContext is null!");
        ConfigurableApplicationContext applicationContexts = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContexts.getBeanFactory();
        beanFactory.removeBeanDefinition(beanId);
    }

    public static void removeBeans(String... beanIds) {
        if (beanIds == null || beanIds.length == 0) {
            return;
        }
        Preconditions.checkNotNull(applicationContext, "Spring ApplicationContext is null!");
        ConfigurableApplicationContext applicationContexts = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContexts.getBeanFactory();
        for (String beanId : beanIds) {
            if (beanId != null && !beanId.isEmpty() && beanFactory.isBeanNameInUse(beanId)) {
                beanFactory.removeBeanDefinition(beanId);
            }
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
