package com.yxt.ucache.common;

/**
 * 用于创建 Cache 实例的工具类
 *
 * @author qinjiaxing on 2025/2/2
 * @author <others>
 */
public interface CacheBuilder {

    /**
     * 构建缓存实例对象
     *
     * @return CacheClient
     */
    CacheClient buildCache();

}
