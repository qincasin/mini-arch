package com.qjx.mini.beans.factory.annotation;

import com.qjx.mini.beans.factory.BeanFactory;
import com.qjx.mini.beans.factory.BeansException;
import com.qjx.mini.beans.factory.config.BeanPostProcessor;
import java.lang.reflect.Field;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/17
 * @author <others>
 */
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {

    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Object result = bean;
        Class<?> clazz = result.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        if (declaredFields == null) {
            return result;
        }
        // 对每一个属性进行判断，如果带有@Autowired注解则进行处理
        for (Field field : declaredFields) {
            boolean annotationPresent = field.isAnnotationPresent(Autowired.class);
            if (annotationPresent) {
                // 根据属性名查找同名的bean
                String name = field.getName();
                Object autuwiredObject = this.getBeanFactory().getBean(name);
                // 设置属性值，完成注入
                try {
                    field.setAccessible(true);
                    field.set(bean, autuwiredObject);
                    System.out.println("autowire " + name + " for bean " + beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // TODO Auto-generated method stub
        return null;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
