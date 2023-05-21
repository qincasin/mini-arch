package com.qjx.mini.core.env;

/**
 * 基础的 属性的获取
 *
 * @author qinjiaxing on 2023/5/21
 * @author <others>
 */
public interface PropertyResolver {

    boolean containsProperty(String key);

    String getProperty(String key, String defaultValue);

    <T> T getProperty(String key, Class<T> targetType);

    <T> T getProperty(String key, Class<T> targetType, String defaultValue);


    <T> Class<T> getPropertyAsClass(String key, Class<T> targetType);

    String getRequiredProperty(String key) throws IllegalStateException;

    <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;


    String resolvePlaceholder(String text);

    String resolveRequiredPlaceholders(String text) throws IllegalStateException;


}
