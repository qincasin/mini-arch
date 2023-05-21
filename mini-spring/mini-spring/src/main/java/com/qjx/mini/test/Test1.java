package com.qjx.mini.test;


import com.qjx.mini.beans.factory.BeansException;
import com.qjx.mini.context.ClassPathXmlApplicationContext;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/7
 * @author <others>
 */
public class Test1 {

    /**
     * 从最初的简单ApplicationContext拆解成后面的复杂ApplicationContext，我理解起来还是有困难的，努力理解如下，大神勿喷：
     * 1 readxml方法从资源文件读取内容并存入beanDefinitions，这件事情有两个地方不确定，资源的来源不同、资源的格式不同，
     * 抽象的Resource的接口，它的不同子类从不同的来源读取，但是最终都是以Resource接口的形式提供给外部访问的，这样解决了第一个不确定来源的问题；
     * 但是resource接口中被迭代的object又是根据不同格式不同而不同的，element只是xml格式的，
     * 所以又定义了BeanDefinitionReader接口，它的不同子类可以读取不同格式的资源来形成beanDefinition 。
     * 2 . instanceBeans方法取消了 。
     * 3. getBean方法功能增强了，不仅是获得bean，对于未创建的bean还要创建bean
     * 4 新的applicationContext负责组装，可以根据它的名字来体现它的组装功能，例如ClassPathXmlApplicationContext  它组装的Resource的实现类是ClassPathXmlResource  ，
     * 然后因为是xml的，所以需要BeanDefinitionReader的实现类XmlBeanDefinitionReader来读取并注册进beanFactory，
     * 同时ApplicationContext也提供了getBean底层调用beanfactory的实现，提供了registerBeanDefinition  来向底层的beanFactory注册bean。
     * 5 beanFactory 提供了registerBeanDefinition和getBean接口，这样无论是applicationContext还是beanDefinitionReader都可以向它注册beanDefinition，
     * 只要向它注册了，就可以调用它的getBean方法，我一直很纠结为什么不是beanfactory调用不同的beanDefinitionReader，
     * 写完这些，好像有点理解了，这样beanfactory就很专注自己的getBean方法，别的组件要怎么注入，它都不管了。
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        AService aService;
        BaseService bService ;
        try {
            bService = (BaseService) context.getBean("baseservice");
            bService.sayHello();
        }catch (BeansException e){

        }
    }

}
