package com.yxt.ucache.common;

import java.util.function.Function;

/**
 * 缓存序列化策略接口
 *
 * @author qinjiaxing on 2025/1/30
 * @author <others>
 */
public interface SerialGenericPolicy extends SerialPolicy {

    /**
     * 解码(反序列化)
     *
     * @param clazz
     * @param <T>
     * @return
     */
    <T> Function<byte[], T> decoder(Class<T> clazz);
}
