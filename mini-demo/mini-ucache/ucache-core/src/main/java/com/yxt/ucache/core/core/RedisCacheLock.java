package com.yxt.ucache.core.core;

import com.yxt.ucache.common.ValWrapper;

/**
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public class RedisCacheLock extends AbsCacheLock {

    public RedisCacheLock(AbsExternalCache cache) {
        super(cache);
    }

    @Override
    public boolean lock(String key, long mill) throws InterruptedException {
        if (key == null) {
            return false;
        }
        String body = String.valueOf(Thread.currentThread().getId());
        ValWrapper wrapper = ValWrapper.createInstance(mill, body);
        if (cacheProxy.putIfAbsent(key, wrapper)) {
            return true;
        }
        ValWrapper exitValue = cacheProxy.get(key);
        if (exitValue == null || exitValue.getExpireTs() <= System.currentTimeMillis()) {
            return cacheProxy.putIfAbsent(key, wrapper);
        }
        if (exitValue.getValue().toString().startsWith(body)) {
            return true;
        }
        return false;

    }

    @Override
    public boolean lock(String key, long mill, int maxItem) throws InterruptedException {
        return lock(key, mill);
    }

    @Override
    public boolean unlock(String key) {
        if (key == null) {
            return false;
        }
        String body = String.valueOf(Thread.currentThread().getId());
        ValWrapper exitValue = cacheProxy.get(key);
        if (exitValue == null) {
            return true;
        }
        if (exitValue.getValue().toString().startsWith(body)) {
            cacheProxy.remove(key);
            return true;
        }
        return false;
    }
}
