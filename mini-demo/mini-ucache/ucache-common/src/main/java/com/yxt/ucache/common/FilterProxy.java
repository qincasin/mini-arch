package com.yxt.ucache.common;

import com.yxt.ucache.common.enums.OpType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 内部使用, 用于决定 Cache 实例中, 哪些方法需要被 filter 拦截
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FilterProxy {

    OpType opType();

    boolean hasValParam() default false;

}
