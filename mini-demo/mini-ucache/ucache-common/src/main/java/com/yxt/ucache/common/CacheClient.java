package com.yxt.ucache.common;

import com.yxt.ucache.common.enums.OpType;
import com.yxt.ucache.common.uredis.URedisClient;
import java.util.Set;

/**
 * 缓存实例 统一API操作, 用于直接操作缓存内容
 * 每一个缓存实例都对应着一个缓存类型, 比如Redis, caffeine, 当然也可以自定义复合的类型
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
public interface CacheClient extends AutoCloseable {

    @FilterProxy(opType = OpType.GET)
    <K, V> ValWrapper get(String key);

    @FilterProxy(opType = OpType.PUT, hasValParam = true)
    <K, V> void put(String key, @ValParam(paramType = ValWrapper.class) ValWrapper valWrapper);

    @FilterProxy(opType = OpType.PUT, hasValParam = true)
    <K, V> boolean putIfAbsent(String key, @ValParam(paramType = ValWrapper.class) ValWrapper valWrapper);

    @FilterProxy(opType = OpType.REMOVE)
    <K> boolean remove(String key);

    @FilterProxy(opType = OpType.REMOVE)
    <K> boolean removeAll(Set<String> keys);

    default URedisClient getURedisClient() {
        return null;
    }
}
