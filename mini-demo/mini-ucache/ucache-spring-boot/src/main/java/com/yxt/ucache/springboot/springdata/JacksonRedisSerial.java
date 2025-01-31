package com.yxt.ucache.springboot.springdata;

import com.yxt.ucache.common.SerialGenericPolicy;
import java.util.function.Function;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * JacksonRedisSerial
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
public class JacksonRedisSerial implements SerialGenericPolicy {

    static RedisSerializer<Object> jacksonRedisSerializer = RedisSerializer.json();

    @Override
    public <T> Function<byte[], T> decoder(Class<T> clazz) {
        return e -> {
            GenericJackson2JsonRedisSerializer jacksonRedisSerializer = (GenericJackson2JsonRedisSerializer) JacksonRedisSerial.jacksonRedisSerializer;
            return jacksonRedisSerializer.deserialize(e, clazz);
        };
    }

    @Override
    public String name() {
        return Jackson;
    }

    @Override
    public Function<Object, byte[]> encoder() {
        return e -> jacksonRedisSerializer.serialize(e);
    }

    @Override
    public Function<byte[], Object> decoder() {
        return e -> jacksonRedisSerializer.deserialize(e);
    }
}
