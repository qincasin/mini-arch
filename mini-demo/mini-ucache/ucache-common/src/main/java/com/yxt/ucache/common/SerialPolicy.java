package com.yxt.ucache.common;

import java.util.function.Function;

/**
 * 缓存序列化策略接口
 *
 * @author qinjiaxing on 2025/1/30
 * @author <others>
 */
public interface SerialPolicy {

    String STRING = "String";

    String Jackson = "Jackson";

    String Gzip = "Gzip";

    String Protostuff = "Protostuff";

    String name();

    /**
     * 解码（反序列化）
     *
     * @return Function
     */
    Function<Object, byte[]> encoder();

    /**
     * 编码（序列化）
     *
     * @return Function
     */
    Function<byte[], Object> decoder();

}
