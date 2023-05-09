package com.qjx.mini.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * bf 的实现
 *
 * @author qinjiaxing on 2023/5/9
 * @author <others>
 */
public class SimpleBeanFactory implements BeanFactory {

    private List<BeanDefinition> list = new ArrayList<>();
    private List<String> names = new ArrayList<>();
    private Map<String, Object> singleton = new HashMap<>();


    @Override
    public Object getBean(String name) throws NoSuchBeanException {
        Object bean = singleton.get(name);
        if (bean == null) {
            int i = names.indexOf(name);
            if (i == -1) {
                throw new NoSuchBeanException();
            }
            BeanDefinition beanDefinition = list.get(i);
            try {
                Class<?> aClass = Class.forName(beanDefinition.getClassName());
                Object o = aClass.getDeclaredConstructor().newInstance();
                singleton.put(name, o);
                return o;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bean;
    }

    @Override
    public void registerBeanDefinition(BeanDefinition bd) {
        list.add(bd);
        names.add(bd.getId());
    }
}
