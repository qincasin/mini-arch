package com.qjx.mini.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/7
 * @author <others>
 */
public class SimpleBeanFactory implements BeanFactory {

    private List<BeanDefinition> beanDefinitions = new ArrayList<>();
    private List<String> beanNames = new ArrayList<>();
    private Map<String, Object> singletons = new HashMap<>();

    @Override
    public Object getBean(String beanName) throws NoSuchBeanDefinitionException {
        Object singleton = singletons.get(beanName);
        if (singleton == null) {
            int i = beanNames.indexOf(beanName);
            if (i == -1) {
                throw new NoSuchBeanDefinitionException();
            } else {
                BeanDefinition beanDefinition = beanDefinitions.get(i);
                try {
                    Class<?> clazz = Class.forName(beanDefinition.getClassName());
                    singleton = clazz.getDeclaredConstructor().newInstance();
                    singletons.put(beanName, singleton);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return singleton;
    }

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.beanDefinitions.add(beanDefinition);
        this.beanNames.add(beanDefinition.getId());
    }
}
