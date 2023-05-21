package com.qjx.mini.beans.factory.support;

import com.qjx.mini.beans.factory.BeansException;
import com.qjx.mini.beans.factory.config.AbstractAutowireCapableBeanFactory;
import com.qjx.mini.beans.factory.config.BeanDefinition;
import com.qjx.mini.beans.factory.config.ConfigurableListableBeanFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * IOC 引擎
 *
 * @author qinjiaxing on 2023/5/21
 * @author <others>
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory {

    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return (String[]) this.beanDefinitionNames.toArray();
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        // return this.beanDefinitionNames
        //         .stream()
        //         .map(this::getBeanDefinition)
        //         .filter(a -> type.isAssignableFrom(a.getClass()))
        //         .map(BeanDefinition::getClassName)
        //         .toArray(String[]::new);
        List<String> result = new ArrayList<>();
        for (String beanDefinitionName : this.beanDefinitionNames) {
            boolean matchFound = false;
            BeanDefinition mbd = this.getBeanDefinition(beanDefinitionName);
            Class<? extends BeanDefinition> classToMatch = mbd.getClass();
            if (type.isAssignableFrom(classToMatch)) {
                matchFound = true;
            }
            if (matchFound) {
                result.add(mbd.getClassName());
            }

        }
        return (String[]) result.toArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getBeansOfType(Class<?> type) throws BeansException {
        String[] beanNamesForType = getBeanNamesForType(type);
        Map<String, T> result = new LinkedHashMap<>(beanNamesForType.length);
        for (String beanName : beanNamesForType) {
            Object beanInstance = getBean(beanName);
            result.put(beanName, (T) beanInstance);
        }
        return result;
    }


}
