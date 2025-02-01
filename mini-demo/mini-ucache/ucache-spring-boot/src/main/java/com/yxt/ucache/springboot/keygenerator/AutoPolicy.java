package com.yxt.ucache.springboot.keygenerator;

import com.yxt.ucache.anno.CacheRemove;
import com.yxt.ucache.anno.CacheRemoveAll;
import com.yxt.ucache.anno.CacheUpdate;
import com.yxt.ucache.anno.Cached;
import com.yxt.ucache.common.KeyGeneratorPolicy;
import com.yxt.ucache.common.constants.CacheConstants;
import com.yxt.ucache.springboot.aop.AnnoAttributeUtil;
import java.lang.reflect.Method;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AutoPolicy
 * <p>
 * TODO 可以调整为 构造器的方式实现
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
@Component
public class AutoPolicy extends AbsKeyPolicy {

    @Autowired
    private KeyGeneratorPolicy paramStringPolicy;
    @Autowired
    private KeyGeneratorPolicy literalKeyGeneratorPolicy;
    @Autowired
    private KeyGeneratorPolicy spelKeyGeneratorPolicy;
    @Autowired
    private KeyGeneratorPolicy noneKeyGeneratorPolicy;


    @Override
    public Object generateKey(Object target, Method method, Object... args) {
        Cached cached = method.getAnnotation(Cached.class);
        CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
        CacheUpdate cacheUpdate = method.getAnnotation(CacheUpdate.class);
        // 通过反射的方法调用注解中的key()方法，获取到对应的key
        String key = AnnoAttributeUtil.getKey(cached, cacheRemove, cacheUpdate);
        CacheRemoveAll cacheRemoveAll = method.getAnnotation(CacheRemoveAll.class);
        if (cacheRemoveAll != null) {
            return spelKeyGeneratorPolicy.generateKey(target, method, args);
        }
        return parseKey(target, method, key, args);
    }

    private Object parseKey(Object target, Method method, String key, Object[] args) {
        if (StringUtils.isEmpty(key) || CacheConstants.DEFAULT_KEY.equals(target)) {
            if (args == null || args.length == 0) {
                return noneKeyGeneratorPolicy.generateKey(target, method, key, args);
            }
            return paramStringPolicy.generateKey(target, method, key, args);
        }
        try {
            return spelKeyGeneratorPolicy.generateKey(target, method, key, args);
        } catch (Exception e) {
            return literalKeyGeneratorPolicy.generateKey(target, method, key, args);
        }
    }
}
