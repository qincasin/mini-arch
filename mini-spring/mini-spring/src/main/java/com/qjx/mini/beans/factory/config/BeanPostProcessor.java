package com.qjx.mini.beans.factory.config;

import com.qjx.mini.beans.factory.BeanFactory;
import com.qjx.mini.beans.factory.BeansException;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/17
 * @author <others>
 */
public interface BeanPostProcessor {

    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;

    void setBeanFactory(BeanFactory beanFactory);
}
