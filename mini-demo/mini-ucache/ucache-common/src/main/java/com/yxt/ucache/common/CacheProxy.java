package com.yxt.ucache.common;

import com.yxt.ucache.common.enums.CacheType;

/**
 * 用于使用该接口创建Cache代理类的，和Cache接口相比，只是做功能上的区分
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
public interface CacheProxy extends CacheClient {

    default Object serialVal(ValWrapper valWrapper) {
        return valWrapper.getValue();
    }

    default Object deSerialVal(ValWrapper valWrapper) {
        return valWrapper.getValue();
    }

    /**
     * 获取当前代理类的目标类，额就是当前类自己
     *
     * @return
     */
    default CacheProxy unProxy() {
        return this;
    }

    /**
     * 获取当前类的代理类
     *
     * @return
     */
    default CacheProxy getProxy() {
        return null;
    }

    default void setProxy(CacheProxy proxy) {

    }

    default Filter getFilter() {
        return null;
    }

    default void setFilter(Filter filter) {

    }

    /**
     * 获取锁
     * 会根据参数优先选择一个锁, 如果没有对应的, 则选一个可用的锁
     *
     * @param biasBlock 是否 优先选择阻塞锁(JVM锁)
     * @return
     */
    default CacheLock getCacheLocker(boolean biasBlock) {
        return null;
    }

    CacheType getCacheType();


}
