package com.qjx.mini.beans.factory;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/9
 * @author <others>
 */
public interface BeanFactory {

    Object getBean(String name) throws BeansException;
    // void registerBeanDefinition(BeanDefinition bd);
    //
    // void registerBean(String beanName, Object obj);

    boolean containsBean(String name);

    boolean isSingleton(String name);

    boolean isPrototype(String name);

    Class<?> getType(String name);


}
