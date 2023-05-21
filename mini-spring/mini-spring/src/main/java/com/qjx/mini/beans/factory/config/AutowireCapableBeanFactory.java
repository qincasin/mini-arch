package com.qjx.mini.beans.factory.config;

import com.qjx.mini.beans.factory.BeansException;
import com.qjx.mini.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.qjx.mini.beans.factory.support.AbstractBeanFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/17
 * @author <others>
 */
public class AutowireCapableBeanFactory extends AbstractBeanFactory {

    private final List<AutowiredAnnotationBeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public void addBeanPostProcessor(AutowiredAnnotationBeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }

    public List<AutowiredAnnotationBeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (AutowiredAnnotationBeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            beanPostProcessor.setBeanFactory(this);
            result = beanPostProcessor.postProcessBeforeInitialization(result, beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (AutowiredAnnotationBeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            beanPostProcessor.setBeanFactory(this);
            result = beanPostProcessor.postProcessAfterInitialization(result, beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }
}
