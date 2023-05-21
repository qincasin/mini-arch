package com.qjx.mini.beans.factory.support;

import com.qjx.mini.beans.factory.config.SingletonBeanRegister;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/10
 * @author <others>
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegister {

    // 容器中存放所有bean的名称的列表
    protected List<String> beanNames = new ArrayList<>();

    // 容器中存放所有bean实例的map
    protected Map<String, Object> singletons = new ConcurrentHashMap<>();


    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletons) {
            this.singletons.put(beanName, singletonObject);
            this.beanNames.add(beanName);
        }
    }

    @Override
    public Object getSingleton(String beanName) {
        return this.singletons.get(beanName);
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return this.singletons.containsKey(beanName);
    }

    @Override
    public String[] getSingletonNames() {
        return (String[]) this.beanNames.toArray();
    }

    protected void removeSingleton(String beanName) {
        synchronized (this.singletons) {
            this.beanNames.remove(beanName);
            this.singletons.remove(beanName);
        }
    }
}
