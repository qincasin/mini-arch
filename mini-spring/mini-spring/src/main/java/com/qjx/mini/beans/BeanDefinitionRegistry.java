package com.qjx.mini.beans;

/**
 * 一个存放 BeanDefinition 的仓库，可以存放、移除、获取及判断 BeanDefinition 对象
 *
 * @author qinjiaxing on 2023/5/11
 * @author <others>
 */
public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String name, BeanDefinition bd);

    void removeBeanDefinition(String name);

    BeanDefinition getBeanDefinition(String name);

    boolean containsBeanDefinition(String name);

}
