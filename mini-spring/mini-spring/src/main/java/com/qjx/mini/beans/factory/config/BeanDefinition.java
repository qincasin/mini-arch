package com.qjx.mini.beans.factory.config;

import com.qjx.mini.beans.PropertyValues;
import com.qjx.mini.beans.factory.config.ConstructorArgumentValues;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/9
 * @author <others>
 */
public class BeanDefinition {

    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    /**
     * 表示 Bean 要不要在加载的时候初始化，
     */
    private boolean lazyInit = true;

    private String[] dependsOn;
    private ConstructorArgumentValues constructorArgumentValues;
    private PropertyValues propertyValues;

    /**
     * 当一个 Bean 构造好并实例化之后是否要让框架调用初始化方法
     */
    private String initMethodName;

    private volatile Object beanClass;

    private String id;
    private String className;

    private String scope = SCOPE_SINGLETON;


    public BeanDefinition(String id, String className) {
        this.id = id;
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean hasBeanClass() {
        return (this.beanClass instanceof Class);
    }

    public Class<?> getBeanClass() {
        return (Class<?>) this.beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(scope);
    }

    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(scope);
    }

    public boolean isLazyInit() {
        return this.lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String[] getDependsOn() {
        return this.dependsOn;
    }

    public void setDependsOn(String... dependsOn) {
        this.dependsOn = dependsOn;
    }

    public ConstructorArgumentValues getConstructorArgumentValues() {
        return this.constructorArgumentValues;
    }

    public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues =
                (constructorArgumentValues != null ? constructorArgumentValues : new ConstructorArgumentValues());
    }

    public boolean hasConstructorArgumentValues() {
        return !this.constructorArgumentValues.isEmpty();
    }

    public PropertyValues getPropertyValues() {
        return this.propertyValues;
    }

    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = (propertyValues != null ? propertyValues : new PropertyValues());
    }

    public String getInitMethodName() {
        return this.initMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

}
