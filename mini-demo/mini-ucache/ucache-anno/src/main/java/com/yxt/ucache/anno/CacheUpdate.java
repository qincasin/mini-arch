package com.yxt.ucache.anno;


import com.yxt.ucache.common.ExpirePolicy;
import com.yxt.ucache.common.KeyGeneratorPolicy;
import com.yxt.ucache.common.constants.CacheConstants;
import com.yxt.ucache.common.enums.ConsistencyType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Deprecated 不建议使用
 * 缓存建议直接删除 使用注解 @CacheRemove
 */
@Deprecated
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheUpdate {

    String namespace() default CacheConstants.DEFAULT_NAMESPACE;

    String cacheName();

    String key() default CacheConstants.DEFAULT_KEY;

    Class<? extends KeyGeneratorPolicy> keyPolicy() default KeyGeneratorPolicy.class;

    long expire() default CacheConstants.DEFAULT_EXPIRE;

    Class<? extends ExpirePolicy> expirePolicy() default ExpirePolicy.class;

    ConsistencyType consistency() default ConsistencyType.DEFAULT;
}
