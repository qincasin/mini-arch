package com.yxt.ucache.core.core;

import com.yxt.ucache.common.CacheLock;
import com.yxt.ucache.common.CacheProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存锁抽象类
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public abstract class AbsCacheLock implements CacheLock {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected CacheProxy cacheProxy;

    protected AbsCacheLock(CacheProxy cacheProxy) {
        this.cacheProxy = cacheProxy;
    }
}
