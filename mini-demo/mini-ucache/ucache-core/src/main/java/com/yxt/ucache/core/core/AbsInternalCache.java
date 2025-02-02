package com.yxt.ucache.core.core;

import com.yxt.ucache.common.ValWrapper;
import com.yxt.ucache.common.constants.CacheConstants;
import com.yxt.ucache.common.enums.CacheType;
import com.yxt.ucache.core.core.config.InternalConfig;
import com.yxt.ucache.core.event.PubSub;
import com.yxt.ucache.core.exception.CacheInterruptException;
import java.util.Collection;
import java.util.Set;

/**
 * 内部缓存抽象类
 */
public abstract class AbsInternalCache extends AbsCache {

    protected InternalConfig caffeineConfig;
    protected AbsExternalCache remoteCache;

    protected AbsInternalCache(InternalConfig caffeineConfig) {
        super(null, CacheType.LOCAL);
        this.cacheLock = new JvmCacheLock();
        this.caffeineConfig = caffeineConfig;
        registerRemoveConsumer();
    }

    @Override
    public void init() {

    }

    @Override
    public <K, V> ValWrapper get(String key) {
        try {
            return doGet(key);
        } catch (Exception e) {
            logger.error("UCache doGet error, key={}", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_OTHER,
                    "doGet error, key=" + key + e.getMessage());
        }
    }

    public abstract ValWrapper doGet(String key);

    @Override
    public <K, V> void put(String key, ValWrapper valWrapper) {
        try {
            long expire = valWrapper.getExpireTs() - System.currentTimeMillis();
            long configMaxExpire = caffeineConfig.getExpireAfterWrite().toMillis();
            doPut(key, valWrapper, Math.min(expire, configMaxExpire));
        } catch (Exception e) {
            logger.error("UCache doGet error, key={}", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_OTHER,
                    "doGet error, key=" + key + e.getMessage());
        }
    }

    public abstract void doPut(String key, ValWrapper valWrapper, long expire);

    @Override
    public <K, V> boolean putIfAbsent(String key, ValWrapper valWrapper) {
        return false;
    }

    public abstract boolean doPutIfAbsent(String key, ValWrapper valWrapper, long expire);

    @Override
    public <K> boolean remove(String key) {
        try {
            return this.doRemove(key);
        } catch (Exception e) {
            logger.error("UCache doRemove error, key={}", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_OTHER,
                    "doRemove error, key=" + key + e.getMessage());
        }
    }

    public abstract boolean doRemove(String key);

    @Override
    public <K> boolean removeAll(Set<String> keys) {
        try {
            return this.doRemoveAll(keys);
        } catch (Exception e) {
            logger.error("UCache doRemoveAll error, key={}", keys, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_OTHER,
                    "doRemoveAll error, key=" + keys + e.getMessage());
        }
    }

    public abstract boolean doRemoveAll(Collection<String> keys);


    public AbsExternalCache getRemoteCache() {
        return remoteCache;
    }

    public void setRemoteCache(AbsExternalCache remoteCache) {
        this.remoteCache = remoteCache;
    }

    private void registerRemoveConsumer() {
        if (remoteCache != null) {
            remoteCache.addRemoveConsumer(pubsubBody -> {
                if (pubsubBody.getType() == PubSub.TypeRemoveKey) {
                    try {
                        this.removeAll(pubsubBody.getKeys());
                    } catch (Exception e) {
                        logger.warn("sub订阅执行内容 removeAll error ", e);
                    }
                }
            });
            logger.debug("UCache AbsInternalCache 开始订阅 '{}', 监听执行 removeAll", PubSub.TypeRemoveKey);
        }
    }


}
