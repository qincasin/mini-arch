package com.yxt.ucache.core.core;

import com.yxt.ucache.common.ValWrapper;
import com.yxt.ucache.core.core.config.InternalConfig;
import java.util.Collection;

/**
 * <Description>
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public class CaffeineCache extends AbsInternalCache {

    private com.github.benmanes.caffeine.cache.Cache<String, ValWrapper> cache;

    public CaffeineCache(com.github.benmanes.caffeine.cache.Cache<String, ValWrapper> cache, InternalConfig caffeineConfig) {
        super(caffeineConfig);
        this.cache = cache;
        this.cacheClientName = "CaffeineCache";
    }

    @Override
    public ValWrapper doGet(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void doPut(String key, ValWrapper valWrapper, long expire) {
        if (expire <= 0) {
            return;
        }
        valWrapper.setExpire(expire);
        cache.put(key, valWrapper);
    }

    @Override
    public boolean doPutIfAbsent(String key, ValWrapper valWrapper, long expire) {
        if (expire <= 0) {
            return false;
        }
        valWrapper.setExpire(expire);
        return cache.asMap().putIfAbsent(key, valWrapper) == null;
    }

    @Override
    public boolean doRemove(String key) {
        return cache.asMap().remove(key) != null;
    }

    @Override
    public boolean doRemoveAll(Collection<String> keys) {
        cache.invalidateAll();
        return true;
    }

    @Override
    public void close() throws Exception {
        try {
            cache.cleanUp();
        } catch (Exception e) {
            // ignore
            logger.warn(" ", e);
        }
    }
}
