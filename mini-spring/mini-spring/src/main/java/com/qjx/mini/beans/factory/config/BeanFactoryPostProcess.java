package com.qjx.mini.beans.factory.config;

import com.qjx.mini.beans.factory.BeanFactory;
import com.qjx.mini.beans.factory.BeansException;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/21
 * @author <others>
 */
public interface BeanFactoryPostProcess {

    void postProcessBeanFactory(BeanFactory beanFactory) throws BeansException;

}
