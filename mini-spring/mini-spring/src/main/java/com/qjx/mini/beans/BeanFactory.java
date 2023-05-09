package com.qjx.mini.beans;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/9
 * @author <others>
 */
public interface BeanFactory {

    Object getBean(String name) throws NoSuchBeanException;

    void registerBeanDefinition(BeanDefinition bd);


}
