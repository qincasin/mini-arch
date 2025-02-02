package com.yxt.ucache.core.core;

import com.yxt.ucache.common.CacheClient;
import com.yxt.ucache.common.CacheProxy;
import com.yxt.ucache.core.core.config.NamespaceConfig;

/**
 * 默认多级缓存, 可通过配置控制细节
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public class AutoCache extends AbsCombinationCache {

    public AutoCache(NamespaceConfig config, CacheClient localCache, CacheClient remoteCache) {
        super(config, (CacheProxy) localCache, (CacheProxy) remoteCache);
    }

    @Override
    public void close() throws Exception {

        try {
            for (CacheProxy cacheProxy : multiCache) {
                cacheProxy.close();
            }
        } catch (Exception e) {
            // ingnore
            logger.warn("", e);
        }
    }

    @Override
    public String getCacheInfo() {
        String localCacheInfo = "";
        if (internalCache != null) {
            localCacheInfo = internalCache.getCacheInfo();
        }
        String remoteCacheInfo = "";
        if (externalCache != null) {
            remoteCacheInfo = externalCache.getCacheInfo();
        }
        return String.format("%s(%s, %s)", getCacheClientName(), localCacheInfo, remoteCacheInfo);
    }
}
