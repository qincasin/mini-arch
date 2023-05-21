package com.qjx.mini.beans.factory.config;

import com.qjx.mini.beans.factory.ListableBeanFactory;

/**
 * 将 AutowireCapableBeanFactory、ListableBeanFactory 和 ConfigurableBeanFactory 合并在一起
 *
 * @author qinjiaxing on 2023/5/21
 * @author <others>
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

}
