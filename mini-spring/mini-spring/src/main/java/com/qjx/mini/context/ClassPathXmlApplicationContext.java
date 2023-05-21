package com.qjx.mini.context;

import com.qjx.mini.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.qjx.mini.beans.factory.config.BeanFactoryPostProcess;
import com.qjx.mini.beans.factory.config.ConfigurableListableBeanFactory;
import com.qjx.mini.beans.factory.support.DefaultListableBeanFactory;
import com.qjx.mini.beans.factory.xml.XmlBeanDefinitionReader;
import com.qjx.mini.core.ClassPathXmlResource;
import com.qjx.mini.core.Resource;
import java.util.ArrayList;
import java.util.List;

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
public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

    DefaultListableBeanFactory beanFactory;

    private List<BeanFactoryPostProcess> beanFactoryPostProcesses = new ArrayList<>();

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
        Resource resource = new ClassPathXmlResource(fileName);
        // bf
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        // reader
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(bf);
        // load
        reader.loadBeanDefinitions(resource);
        // set bf
        this.beanFactory = bf;
        if (isRefresh) {
            try {
                refresh();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    @Override
    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
    }

    @Override
    protected void initApplicationEventPublisher() {
        ApplicationEventPublisher applicationEventPublisher = new SimpleApplicationEventPublisher();
        this.setApplicationEventPublisher(applicationEventPublisher);
    }

    @Override
    protected void onRefresh() {
        this.beanFactory.refresh();
    }

    @Override
    protected void registerListeners() {
        ApplicationListener listener = new ApplicationListener();
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }

    @Override
    protected void finishRefresh() {
        publishEvent(new ContextRefreshEvent(this));
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        this.getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    public void addApplicationListener(ApplicationListener listener) {
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }
}
