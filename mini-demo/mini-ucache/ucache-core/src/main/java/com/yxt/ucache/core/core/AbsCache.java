package com.yxt.ucache.core.core;

import com.yxt.ucache.common.CacheInit;
import com.yxt.ucache.common.CacheLock;
import com.yxt.ucache.common.CacheProxy;
import com.yxt.ucache.common.Filter;
import com.yxt.ucache.common.enums.CacheType;
import com.yxt.ucache.core.core.config.NamespaceConfig;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象的缓存实现
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
public abstract class AbsCache implements CacheProxy, CacheInit {

    public static final String keyRemoveTopic = "ucache_keyRemoveTopic";
    protected final AtomicBoolean init = new AtomicBoolean(false);
    protected Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 缓存实例对应的配置
     */
    protected NamespaceConfig config;
    protected CacheType cacheType;
    protected CacheLock cacheLock;
    protected String cacheClientName;

    protected CacheProxy proxy;
    protected Filter filter;

    protected AtomicBoolean closed = new AtomicBoolean(false);

    protected AbsCache(NamespaceConfig config, CacheType cacheType) {
        this.config = config;
        this.cacheType = cacheType;
    }

    public NamespaceConfig getConfig() {
        return this.config;
    }

    @Override
    public CacheType getCacheType() {
        return this.cacheType;
    }

    public String getCacheClientName() {
        return cacheClientName;
    }

    public String getCacheInfo() {
        return getCacheType().getVal() + " " + getCacheClientName();
    }

    /**
     * 获取锁
     * 会根据参数优先选择一个锁, 如果没有对应的, 则选一个可用的锁
     *
     * @param biasBlock 是否 优先选择阻塞锁(JVM锁)
     * @return
     */
    @Override
    public CacheLock getCacheLocker(boolean biasBlock) {
        return cacheLock;
    }

    @Override
    public CacheProxy getProxy() {
        return proxy;
    }

    @Override
    public void setProxy(CacheProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
