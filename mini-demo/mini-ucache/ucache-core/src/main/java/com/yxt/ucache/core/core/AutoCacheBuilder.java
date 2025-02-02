package com.yxt.ucache.core.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yxt.ucache.common.CacheClient;
import com.yxt.ucache.common.CacheProxy;
import com.yxt.ucache.core.core.config.CacheReporterConfig;
import com.yxt.ucache.core.core.config.NamespaceConfig;
import com.yxt.ucache.core.event.CacheOpEventPublisher;
import com.yxt.ucache.core.event.RedisCacheManagerListener;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * AutoCacheBuilder
 *
 * @author qinjiaxing on 2025/2/2
 * @author <others>
 */
public class AutoCacheBuilder extends AbstractCacheBuilder<AutoCacheBuilder> {

    private final CacheProxy local;
    private final CacheProxy remote;

    public AutoCacheBuilder(NamespaceConfig namespaceConfig, CacheProxy local, CacheProxy remote) {
        super(namespaceConfig);
        this.local = local;
        this.remote = remote;
    }

    @Override
    public CacheClient buildCache() {
        AutoCache cache = new AutoCache(namespaceConfig, local, remote);
        addHeadFilter(cache);
        return createCacheProxy(cache);
    }

    @Override
    protected void preBuild() {
        super.preBuild();
        String namespace = namespaceConfig.getNamespace();
        // 添加拦截器
        // 事件发布器
        CacheOpEventPublisher cacheOpEventPublisher = new CacheOpEventPublisher(namespace);
        addFilters(cacheOpEventPublisher);
        int processors = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(processors, processors * 4, 300000, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(5000),
                new ThreadFactoryBuilder().setDaemon(true).setNameFormat("CacheOpEventPublisher-").build(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        cacheOpEventPublisher.setExecutor(executor);
        // 添加事件监听器 缓存key管理监听
        CacheReporterConfig cacheReporter = namespaceConfig.getParent().getCacheReporter();
        if (cacheReporter != null && cacheReporter.isEnabled()) {
            RedisCacheManagerListener cacheManagerListener = new RedisCacheManagerListener();
            UCacheManager.addEventListener(cacheManagerListener);
        }
    }
}
