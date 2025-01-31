package com.yxt.ucache.core.core;

import com.yxt.ucache.common.SerialPolicy;
import com.yxt.ucache.core.utils.InnerProtostuffUtils;
import java.util.function.Function;

public class ProtostuffSerial implements SerialPolicy {

    private Class wrappClass;

    public ProtostuffSerial(Class wrappClass) {
        this.wrappClass = wrappClass;
    }

    @Override
    public String name() {
        return Protostuff;
    }

    @Override
    public Function<Object, byte[]> encoder() {
        return e -> {
            if (e == null) {
                return null;
            }
            return InnerProtostuffUtils.serialize(e);
        };
    }

    @Override
    public Function<byte[], Object> decoder() {
        return e -> {
            if (e == null) {
                return null;
            }
            return InnerProtostuffUtils.deserialize(e, wrappClass);
        };
    }
}
