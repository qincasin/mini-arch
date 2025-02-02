package com.yxt.ucache.core.core;

/**
 * 啥也没用的类
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public class NoneLock extends AbsCacheLock {

    public NoneLock() {
        super(null);
    }

    @Override
    public boolean lock(String key, long mill) {
        return true;
    }

    @Override
    public boolean lock(String key, long mill, int maxItem) {
        return true;
    }

    @Override
    public boolean unlock(String key) {
        return true;
    }
}
