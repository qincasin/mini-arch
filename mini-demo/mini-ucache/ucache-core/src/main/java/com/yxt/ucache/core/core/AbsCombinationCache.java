package com.yxt.ucache.core.core;

import com.yxt.ucache.common.CacheLock;
import com.yxt.ucache.common.CacheProxy;
import com.yxt.ucache.common.ValWrapper;
import com.yxt.ucache.common.enums.CacheType;
import com.yxt.ucache.common.uredis.URedisClient;
import com.yxt.ucache.core.core.config.NamespaceConfig;
import com.yxt.ucache.core.utils.InnerAssertUtils;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <Description>
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public abstract class AbsCombinationCache extends AbsCache {

    protected final List<CacheProxy> multiCache;
    protected final List<CacheStatus> inValidCache;
    protected AbsExternalCache externalCache;
    protected AbsInternalCache internalCache;
    protected boolean ignoreDisabled = true;


    public AbsCombinationCache(NamespaceConfig config, CacheProxy... multiCache) {
        super(config, CacheType.BOTH);
        InnerAssertUtils.isTrue(multiCache != null && multiCache.length > 0, "'multiCache' must be have value");
        this.multiCache = Arrays.stream(multiCache).filter(Objects::nonNull).collect(Collectors.toList());
        this.inValidCache = new CopyOnWriteArrayList<>(new CacheStatus[this.multiCache.size()]);
        for (int i = this.multiCache.size() - 1; i >= 0; i--) {
            CacheProxy cache = this.multiCache.get(i);
            CacheType cacheType = cache.getCacheType();
            if (CacheType.REMOTE.equals(cacheType)) {
                this.externalCache = (AbsExternalCache) cache.unProxy();
            }
            if (CacheType.LOCAL.equals(cacheType)) {
                this.internalCache = (AbsInternalCache) cache.unProxy();
            }
            if (externalCache != null && internalCache != null) {
                break;
            }
        }
    }

    @Override
    public void init() {

    }

    @Override
    public <K, V> ValWrapper get(String key) {
        ValWrapper valWrapper = null;
        int hitIndex = 0;
        CacheProxy hitCache;
        for (; hitIndex < multiCache.size(); hitIndex++) {
            if (!validCache(hitIndex)) {
                continue;
            }
            hitCache = multiCache.get(hitIndex);
            valWrapper = hitCache.get(key);
            if (valWrapper != null) {
                if (config.getParent().isDebug()) {
                    logger.debug("UCache 多级缓存命中 {} 级, key = {}", hitIndex, key);
                }
                break;
            }
        }
        if (valWrapper == null) {
            return null;
        }
        fillOtherCache(hitIndex, key, valWrapper);
        return valWrapper;
    }

    private void fillOtherCache(int hitIndex, String key, ValWrapper valWrapper) {
        for (int index = hitIndex - 1; index >= 0; index--) {
            if (!validCache(index)) {
                continue;
            }
            CacheProxy thisCache = multiCache.get(index);
            thisCache.put(key, valWrapper);
        }
    }

    @Override
    public <K, V> void put(String key, ValWrapper valWrapper) {
        int lastIndex = 0;
        for (int index = multiCache.size() - 1; index >= 0; index--) {
            if (!validCache(index)) {
                continue;
            }
            lastIndex = index;
            CacheProxy lastCache = multiCache.get(index);
            lastCache.put(key, valWrapper);
            break;
        }
        removeOtherCache(lastIndex, key);
    }

    private void removeOtherCache(int lastIndex, String key) {
        for (int index = lastIndex - 1; index >= 0; index--) {
            if (!validCache(index)) {
                continue;
            }
            multiCache.get(index).remove(key);
        }
    }


    @Override
    public <K, V> boolean putIfAbsent(String key, ValWrapper valWrapper) {
        boolean putIfAbsent = true;
        int lastIndex = multiCache.size() - 1;
        for (int i = multiCache.size() - 1; i >= 0; i--) {
            if (!validCache(i)) {
                continue;
            }
            lastIndex = i;
            CacheProxy lastCache = multiCache.get(i);
            putIfAbsent = lastCache.putIfAbsent(key, valWrapper);
            if (!putIfAbsent) {
                return false;
            }
            break;
        }
        removeOtherCache(lastIndex, key);
        return putIfAbsent;
    }

    @Override
    public <K> boolean remove(String key) {
        boolean ok = true;
        for (int i = multiCache.size() - 1; i >= 0; i--) {
            if (!validCache(i)) {
                continue;
            }
            ok &= multiCache.get(i).remove(key);
        }
        return ok;
    }

    @Override
    public <K> boolean removeAll(Set<String> keys) {
        boolean ok = true;
        for (int i = multiCache.size() - 1; i >= 0; i--) {
            if (!validCache(i)) {
                continue;
            }
            ok &= multiCache.get(i).removeAll(keys);
        }
        return ok;
    }

    @Override
    public CacheLock getCacheLocker(boolean biasBlock) {
        return new LockerAutoSelect(externalCache.cacheLock, internalCache.cacheLock, biasBlock);
    }

    @Override
    public URedisClient getURedisClient() {
        return externalCache.getURedisClient();
    }

    protected boolean validCache(int currentIndex) {
        CacheStatus cacheStatus = inValidCache.get(currentIndex);
        boolean valid = cacheStatus == null || !ignoreDisabled;
        if (!valid) {
            // 缓存实例失效
            return false;
        }
        if (CacheType.LOCAL.equals(multiCache.get(currentIndex).getCacheType()) && !enableLocal()) {
            // 本次缓存未开启
            return false;
        }
        return true;
    }

    private boolean enableLocal() {
        return config.getParent().getLocal().isEnabled();
    }

    static class CacheStatus {

        AbsCache cache;
        LocalDateTime lastTime;
        int status;
    }

    /**
     * 自动根据需要选择可用的锁，最终退化为jvm锁
     */
    static class LockerAutoSelect implements CacheLock {

        private static Logger logger = LoggerFactory.getLogger(LockerAutoSelect.class);
        private volatile CacheLock redisLock; // NOSONAR
        private volatile CacheLock jvmLock; // NOSONAR
        private volatile boolean biasBlock;

        public LockerAutoSelect(CacheLock redisLock, CacheLock jvmLock, boolean biasBlock) {
            InnerAssertUtils.isTrue(redisLock != null || jvmLock != null, "redisLock | jvmLock 不能为空");
            this.redisLock = redisLock;
            this.jvmLock = jvmLock;
            this.biasBlock = biasBlock;
        }

        @Override
        public boolean lock(String key, long mill) throws InterruptedException {
            return lockDelegate(key, mill, 1);
        }

        private boolean lockDelegate(String key, long mill, int times) throws InterruptedException {
            boolean useRedis = false;
            try {
                if (biasBlock || redisLock == null) {
                    return jvmLock.lock(key, mill);
                }
                useRedis = true;
                return redisLock.lock(key, mill);
            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e) {
                if (useRedis) {
                    redisLock = null;
                    logger.warn("redisLock lock 异常, 将其排除, key={}", key, e);
                    if (times > 0) {
                        return lockDelegate(key, mill, times - 1);
                    }
                }
            }
            return false;
        }

        @Override
        public boolean lock(String key, long mill, int maxItem) throws InterruptedException {
            return lockDelegate(key, mill, maxItem, 1);
        }

        private boolean lockDelegate(String key, long mill, int maxItem, int times) throws InterruptedException {
            boolean useRedis = false;
            try {
                if (biasBlock || redisLock == null) {
                    return jvmLock.lock(key, mill, maxItem);
                }
                useRedis = true;
                return redisLock.lock(key, mill, maxItem);
            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e) {
                if (useRedis) {
                    redisLock = null;
                    logger.warn("redisLock lock 异常, 将其排除, key={}", key, e);
                    if (times > 0) {
                        return lockDelegate(key, mill, maxItem, times - 1);
                    }
                }
            }
            return false;
        }

        @Override
        public boolean unlock(String key) {
            return unlockDelegate(key);
        }

        private boolean unlockDelegate(String key) {
            boolean useRedis = false;
            try {
                if (biasBlock || redisLock == null) {
                    return jvmLock.unlock(key);
                }
                useRedis = true;
                return redisLock.unlock(key);
            } catch (Exception e) {
                if (useRedis) {
                    logger.warn("redisLock 异常, 将其排除, key={}", key, e);
                    redisLock = null;
                }
                return false;
            }
        }
    }
}
