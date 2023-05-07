package com.qjx.mini.beans;

import com.qjx.mini.core.Resource;
import org.dom4j.Element;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/7
 * @author <others>
 */
public class XmlBeanDefinitionReader {

    private BeanFactory beanFactory;

    public XmlBeanDefinitionReader(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element next = (Element) resource.next();
            String beanId = next.attributeValue("id");
            String beanClassName = next.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanId, beanClassName);
            this.beanFactory.registerBeanDefinition(beanDefinition);
        }
    }
}
