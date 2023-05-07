package com.qjx.mini.beans;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/7
 * @author <others>
 */
public interface BeanFactory {

    Object getBean(String beanName) throws NoSuchBeanDefinitionException;

    void registerBeanDefinition(BeanDefinition beanDefinition);

}
