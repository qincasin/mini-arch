package com.qjx.mini.beans.factory.config;

import com.qjx.mini.beans.factory.BeanFactory;
import com.qjx.mini.beans.factory.BeansException;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/17
 * @author <others>
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

    int AUTOWIRE_NO = 0;
    int AUTOWIRE_BY_NAME = 1;
    int AUTOWIRE_BY_TYPE = 2;

    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException ;

     Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException ;
}
