package com.qjx.mini.context;

import com.qjx.mini.beans.BeanDefinition;
import com.qjx.mini.beans.BeanFactory;
import com.qjx.mini.beans.NoSuchBeanDefinitionException;
import com.qjx.mini.beans.SimpleBeanFactory;
import com.qjx.mini.beans.XmlBeanDefinitionReader;
import com.qjx.mini.core.ClassPathXmlResource;
import com.qjx.mini.core.Resource;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

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
public class ClassPathXmlApplicationContext implements BeanFactory {

    private BeanFactory beanFactory;

    /**
     * 改造后做的事情：
     * - 解析 XML 文件中的内容。
     * - 加载解析的内容，构建 BeanDefinition。
     * - 读取 BeanDefinition 的配置信息，实例化 Bean，然后把它注入到 BeanFactory 容器中。
     *
     * @param fileName
     */
    public ClassPathXmlApplicationContext(String fileName) {
        Resource resource = new ClassPathXmlResource(fileName);
        BeanFactory bf = new SimpleBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(bf);
        reader.loadBeanDefinitions(resource);
        this.beanFactory = bf;
    }

    @Override
    public Object getBean(String beanName) throws NoSuchBeanDefinitionException {
        return this.beanFactory.getBean(beanName);
    }

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.beanFactory.registerBeanDefinition(beanDefinition);
    }
    // private List<BeanDefinition> beanDefinitions = new ArrayList<>();
    // private Map<String, Object> singletons = new HashMap<>();
    //
    // public ClassPathXmlApplicationContext(String fileName) {
    //     this.readXml(fileName);
    //     this.instanceBeans();
    // }
    //
    // private void readXml(String fileName) {
    //     SAXReader saxReader = new SAXReader();
    //     try {
    //         URL xmlPath = this.getClass().getClassLoader().getResource(fileName);
    //         Document document = saxReader.read(xmlPath);
    //         Element rootElement = document.getRootElement();
    //         // 对配置文件中的每一个，进行处理
    //         for (Object o : rootElement.elements()) {
    //             Element element = (Element) o;
    //             String id = element.attributeValue("id");
    //             String className = element.attributeValue("class");
    //             BeanDefinition beanDefinition = new BeanDefinition(id, className);
    //             // 将bean 的定义存放到 beanDefinitions
    //             beanDefinitions.add(beanDefinition);
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
    //
    // private void instanceBeans() {
    //     for (BeanDefinition beanDefinition : beanDefinitions) {
    //         try {
    //             singletons.put(beanDefinition.getId(), Class.forName(beanDefinition.getClassName()).getDeclaredConstructor().newInstance());
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //         }
    //     }
    // }
    // public Object getBean(String beanName) {
    //     return singletons.get(beanName);
    // }
}
