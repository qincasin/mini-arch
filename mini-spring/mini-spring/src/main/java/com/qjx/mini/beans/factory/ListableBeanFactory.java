package com.qjx.mini.beans.factory;

import java.util.Map;

/**
 * - 将factory内部管理的bean 作为一个集合来对待
 * - 获取bean的数量
 * - 得到所有bean的名字
 * - 按照某个类型获取bean列表
 *
 * @author qinjiaxing on 2023/5/21
 * @author <others>
 */
public interface ListableBeanFactory extends BeanFactory {

    /**
     * 是否包含 这个bd-name
     * @param beanName
     * @return
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * bd 数量
     * @return
     */
    int getBeanDefinitionCount();

    /**
     * bd-name 名字列表
     * @return
     */
    String[] getBeanDefinitionNames();

    /**
     * 根据type 获取 bead-name 列表
     * @param type
     * @return
     */
    String[] getBeanNamesForType(Class<?> type);

    /**
     * 获取某一类型的bean
     * @param type
     * @return
     * @param <T>
     * @throws BeansException
     */
    <T> Map<String, T> getBeansOfType(Class<?> type) throws BeansException;
}
