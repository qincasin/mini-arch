package com.yxt.ucache.springboot.keygenerator;

import com.yxt.ucache.anno.CacheRemove;
import com.yxt.ucache.anno.CacheRemoveAll;
import com.yxt.ucache.anno.CacheUpdate;
import com.yxt.ucache.anno.Cached;
import com.yxt.ucache.springboot.aop.AnnoAttributeUtil;
import java.lang.reflect.Method;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * LiteralKeyGeneratorPolicy
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
@Component
public class LiteralKeyGeneratorPolicy extends AbsKeyPolicy {

    @Override
    public Object generateKey(Object target, Method method, Object... args) {
        Cached cached = method.getAnnotation(Cached.class);
        CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
        CacheRemoveAll cacheRemoveAll = method.getAnnotation(CacheRemoveAll.class);
        CacheUpdate cacheUpdate = method.getAnnotation(CacheUpdate.class);
        String key = AnnoAttributeUtil.getKey(cached, cacheRemove, cacheUpdate);
        if (StringUtils.isEmpty(key) && cacheRemoveAll == null) {
            return "";
        }
        return key;
    }
}
