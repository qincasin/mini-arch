package com.yxt.ucache.core.core;

import com.yxt.ucache.common.CacheClient;
import com.yxt.ucache.core.core.config.NamespaceConfig;

/**
 * JedisCacheBuilder
 *
 * @author qinjiaxing on 2025/2/2
 * @author <others>
 */
public class JedisCacheBuilder extends ExternalCacheBuilder<JedisCacheBuilder> {

    public JedisCacheBuilder(NamespaceConfig namespaceConfig) {
        super(namespaceConfig);
    }

    public static JedisCacheBuilder createBuilder(NamespaceConfig namespaceConfig) {
        return new JedisCacheBuilder(namespaceConfig);
    }

    @Override
    public CacheClient buildCache() {
        JedisCache cache = new JedisCache(namespaceConfig);
        // 追加tail
        addTailFilter(cache);
        return createCacheProxy(cache);
    }
}
