package com.qjx.mini.beans.factory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <Description>
 * Autowired 修饰成员变量（属性），并且在运行时生效
 *
 * @author qinjiaxing on 2023/5/17
 * @author <others>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {

}
