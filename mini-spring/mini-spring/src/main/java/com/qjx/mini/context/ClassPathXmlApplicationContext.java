package com.qjx.mini.context;

import com.qjx.mini.beans.factory.BeanFactory;
import com.qjx.mini.beans.BeansException;
import com.qjx.mini.beans.factory.support.SimpleBeanFactory;
import com.qjx.mini.beans.factory.xml.XmlBeanDefinitionReader;
import com.qjx.mini.core.ClassPathXmlResource;

/**
 * <Description>
 * ClassPathXmlApplicationContext 定义了唯一的构造函数，构造函数里会做两件事：
 * <p>
 * 一是提供一个 readXml() 方法，通过传入的文件路径，也就是 XML 文件的全路径名，来获取 XML 内的信息，
 * <p>
 * 二是提供一个 instanceBeans() 方法，根据读取到的信息实例化 Bean
 *
 * @author qinjiaxing on 2023/5/7
 * @author <others>
 */
public class ClassPathXmlApplicationContext implements BeanFactory, ApplicationEventPublisher {

    private SimpleBeanFactory beanFactory;

    /**
     * 改造后做的事情：
     * - 解析 XML 文件中的内容。
     * - 加载解析的内容，构建 BeanDefinition。
     * - 读取 BeanDefinition 的配置信息，实例化 Bean，然后把它注入到 BeanFactory 容器中。
     *
     * @param fileName
     */
    public ClassPathXmlApplicationContext(String fileName) {
        this(fileName, true);
    }


    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh) {
        ClassPathXmlResource resource = new ClassPathXmlResource(fileName);
        SimpleBeanFactory bf = new SimpleBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(bf);
        reader.loadBeanDefinitions(resource);
        this.beanFactory = bf;
        if (isRefresh) {
            this.beanFactory.refresh();
        }
    }


    @Override
    public void publishEvnet(ApplicationEvent event) {
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return this.beanFactory.getBean(name);
    }

    @Override
    public boolean containsBean(String name) {
        return this.beanFactory.containsBean(name);
    }

    @Override
    public boolean isSingleton(String name) {
        return false;
        // return this.beanFactory.isSingleton(name);
    }

    @Override
    public boolean isPrototype(String name) {
        return false;
        // return this.beanFactory.isPrototype(name);
    }

    @Override
    public Class<?> getType(String name) {
        return null;
        // return this.beanFactory.getType(name);
    }

}
