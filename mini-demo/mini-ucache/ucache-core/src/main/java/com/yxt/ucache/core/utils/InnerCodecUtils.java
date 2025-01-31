package com.yxt.ucache.core.utils;

import com.yxt.ucache.common.SerialGenericPolicy;
import com.yxt.ucache.common.SerialPolicy;
import com.yxt.ucache.common.ValWrapper;
import com.yxt.ucache.core.core.UCacheManager;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * codec utils
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
public final class InnerCodecUtils {

    private static Logger logger = LoggerFactory.getLogger(LoggerFactory.class);

    private InnerCodecUtils() {
    }

    /**
     * 反序列化
     *
     * @param valWrapper
     * @return
     */
    public static Object deSerialVal(ValWrapper valWrapper) {
        SerialPolicy valueSerialPolicy = UCacheManager.getSerialPolicy(valWrapper.getValueSerialName(), null);
        SerialPolicy valueCompressSerialPolicy = UCacheManager.getSerialPolicy(valWrapper.getValueCompSerialName(), null);
        // 存在压缩策略
        if (valueCompressSerialPolicy != null) {
            // 解压数据
            byte[] unCompress = (byte[]) InnerCodecUtils.decode(valueCompressSerialPolicy, valWrapper.getValue());
            // 使用对应的反序列化方式进行反序列化
            if (valueSerialPolicy != null) {
                return decode(valueSerialPolicy, unCompress);
            }
        }
        // 不存在压缩策略,存在序列化，直接反序列化
        if (valueSerialPolicy != null) {
            return decode(valueSerialPolicy, valWrapper.getValue());
        }
        // 两者都不存在，直接返回原始值
        return valWrapper.getValue();
    }

    /**
     * 序列化
     *
     * @param valWrapper
     * @param valueSerial
     * @param valueCompressSerial
     * @param compressThreshold
     * @return
     */
    public static Object serialVal(ValWrapper valWrapper, SerialPolicy valueSerial, SerialPolicy valueCompressSerial, int compressThreshold) {
        byte[] val = null;
        if (valWrapper.getValue() == null) {
            return val;
        }
        val = encode(valueSerial, valWrapper.getValue());
        if (val != null) {
            if (val.length >= compressThreshold && valueCompressSerial != null) {
                val = encode(valueCompressSerial, val);
                // 设置value 压缩 策略 序列化方式
                valWrapper.setValueCompSerialName(valueCompressSerial.name());
            }
            // 设置 value 序列化方式
            valWrapper.setValueSerialName(valueSerial.name());
            valWrapper.setSize(val.length);
        }
        return val;
    }

    public static byte[] encode(SerialPolicy serialPolicy, Object val) {
        if (isByteArray(val)) {
            return (byte[]) val;
        }
        return serialPolicy.encoder().apply(val);
    }

    public static byte[][] encodeMany(SerialPolicy serialPolicy, Object... vals) {
        byte[][] many = new byte[vals.length][];
        for (int i = 0; i < vals.length; i++) {
            many[i] = encode(serialPolicy, vals[i]);
        }
        return many;
    }

    public static Object decode(SerialPolicy serialPolicy, Object val) {
        if (!isByteArray(val)) {
            return val;
        }
        return serialPolicy.decoder().apply((byte[]) val);
    }


    public static Object decode(SerialPolicy serialPolicy, SerialPolicy backSerialPolicy, Object val) {
        if (!isByteArray(val)) {
            return val;
        }
        try {
            return serialPolicy.decoder().apply((byte[]) val);
        } catch (Exception ex) {
            try {
                return backSerialPolicy.decoder().apply((byte[]) val);
            } catch (Exception e) {
                logger.error("val {} serial error :", val, ex);
                throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
            }
        }
    }

    public static <T> T decode(SerialGenericPolicy serialPolicy, SerialPolicy backSerialPolicy, Object val, Class<T> tClass) {
        if (!isByteArray(val)) {
            return (T) val;
        }
        try {
            return serialPolicy.decoder(tClass).apply((byte[]) val);
        } catch (Exception e) {
            try {
                return (T) backSerialPolicy.decoder().apply((byte[]) val);
            } catch (Exception ex) {
                logger.error("val {} decode error1 :", val, e);
                logger.error("val {} decode error2 :", val, ex);
                throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
            }
        }
    }

    public static Object[] decodeMany(SerialPolicy serialPolicy, Object... vals) {
        Object[] many = new Object[vals.length];
        for (int i = 0; i < vals.length; i++) {
            many[i] = decode(serialPolicy, vals[i]);
        }
        return many;
    }

    private static boolean isByteArray(Object val) {
        return val instanceof byte[];
    }

    private static byte[] getByte(Object val) {
        if (val == null) {
            return new byte[0];
        }
        if (isByteArray(val)) {
            return (byte[]) val;
        }
        if (val == String.class) {
            return val.toString().getBytes(StandardCharsets.UTF_8);
        }
        return val.toString().getBytes(StandardCharsets.UTF_8);
    }
}
