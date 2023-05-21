package com.qjx.mini.beans.factory.config;

import com.qjx.mini.beans.factory.BeanFactory;

/**
 * 维护 Bean 之间的依赖关系以及支持 Bean 处理器也看作一个独立的特性
 *
 * @author qinjiaxing on 2023/5/21
 * @author <others>
 */
public interface ConfigurableBeanFactory extends BeanFactory, SingletonBeanRegister {

    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    int getBeanPostProcessorCount();

    void registerDependentBean(String beanName, String dependentBeanName);

    String[] getDependentBeans(String beanName);

    String[] getDependenciesForBean(String name);

}
