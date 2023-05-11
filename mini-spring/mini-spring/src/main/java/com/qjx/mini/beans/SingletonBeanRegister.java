package com.qjx.mini.beans;

/**
 * 管理单例bean
 *
 * @author qinjiaxing on 2023/5/10
 * @author <others>
 */
public interface SingletonBeanRegister {

    void registerSingleton(String beanName, Object singletonObject);

    Object getSingleton(String beanName);

    boolean containsSingleton(String beanName);

    String[] getSingletonNames();

}
