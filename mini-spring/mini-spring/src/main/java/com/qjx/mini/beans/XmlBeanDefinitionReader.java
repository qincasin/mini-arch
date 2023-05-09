package com.qjx.mini.beans;

import com.qjx.mini.core.Resource;
import org.dom4j.Element;

/**
 * xml 转 bd
 *
 * @author qinjiaxing on 2023/5/9
 * @author <others>
 */
public class XmlBeanDefinitionReader {

    private BeanFactory beanFactory;

    public XmlBeanDefinitionReader(BeanFactory bf) {
        this.beanFactory = bf;
    }

    public void loadResource(Resource resource) {
        while (resource.hasNext()) {
            Element next = (Element) resource.next();
            String beanId = next.attributeValue("id");
            String name = next.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanId, name);
            this.beanFactory.registerBeanDefinition(beanDefinition);
        }
    }
}
