package com.qjx.mini.beans;

import com.qjx.mini.core.Resource;
import java.util.List;
import org.dom4j.Element;

/**
 * xml 转 bd
 *
 * @author qinjiaxing on 2023/5/9
 * @author <others>
 */
public class XmlBeanDefinitionReader {

    private SimpleBeanFactory bf;

    public XmlBeanDefinitionReader(SimpleBeanFactory bf) {
        this.bf = bf;
    }

    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element next = (Element) resource.next();
            String beanId = next.attributeValue("id");
            String name = next.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanId, name);
            // 处理属性
            List<Element> elements = next.elements("property");
            PropertyValues propertyValues = new PropertyValues();
            for (Element element : elements) {
                String type = element.attributeValue("type");
                String n = element.attributeValue("name");
                String value = element.attributeValue("value");
                propertyValues.addPropertyValue(new PropertyValue(type, n, value));
            }
            beanDefinition.setPropertyValues(propertyValues);
            // 处理构造器
            ArgumentValues argumentValues = new ArgumentValues();
            List<Element> constructors = next.elements("constructor-arg");
            for (Element constructor : constructors) {
                String pType = constructor.attributeValue("type");
                String pName = constructor.attributeValue("name");
                String pValue = constructor.attributeValue("value");
                argumentValues.addArgumentValue(new ArgumentValue(pType, pName, pValue));
            }
            beanDefinition.setConstructorArgumentValues(argumentValues);
            this.bf.registerBeanDefinition(beanId, beanDefinition);
        }
    }
}
