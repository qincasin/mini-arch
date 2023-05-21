package com.qjx.mini.context;

import com.qjx.mini.beans.factory.BeansException;
import com.qjx.mini.beans.factory.ListableBeanFactory;
import com.qjx.mini.beans.factory.config.BeanFactoryPostProcess;
import com.qjx.mini.beans.factory.config.ConfigurableBeanFactory;
import com.qjx.mini.beans.factory.config.ConfigurableListableBeanFactory;
import com.qjx.mini.core.env.Environment;
import com.qjx.mini.core.env.EnvironmentCapable;

/**
 * 公共接口
 * - 支持上下文环境 + 事件发布
 * 所有的上下文都实现此接口
 *
 * @author qinjiaxing on 2023/5/21
 * @author <others>
 */
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, ConfigurableBeanFactory, ApplicationEventPublisher {

    String getApplicationName();

    long getStartupDate();

    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

    Environment getEnvironment();

    void setEnvironment(Environment environment);

    void addBeanFactorPostProcessor(BeanFactoryPostProcess postProcess);

    void refresh() throws BeansException, IllegalStateException;

    void close();

    boolean isActive();


}
