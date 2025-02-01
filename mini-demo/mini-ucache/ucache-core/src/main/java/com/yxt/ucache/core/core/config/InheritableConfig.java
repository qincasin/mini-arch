package com.yxt.ucache.core.core.config;

import com.yxt.ucache.common.constants.CacheConstants;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * 可继承的配置类
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
@Setter
@Getter
public class InheritableConfig<P extends InheritableConfig> {

    private P parent;

    private Duration expire;

    private String consistency;

    private String keySerialName;

    private String valueWrapperSerialName;

    private String valueCompressSerialName;

    private String valueSerialName;

    private String uredisValueSerialName;

    private int compressThreshold = CacheConstants.COMPRESS_THRESHOLD;

    /**
     * 是否存null值
     */
    private Boolean cacheNullValue = CacheConstants.DEFAULT_CACHE_NULL_VALUE;

    /**
     * null 值存储过期时间
     */
    private Duration nullValueExpire = Duration.ofMillis(CacheConstants.NULL_VALUE_EXPIRE);

    /**
     * 穿透后加锁时间
     */
    private Duration lockTime = Duration.ofSeconds(3);

    public Duration getExpire() {
        if ((expire == null || expire.toMillis() < 0) && parent != null) {
            return parent.getExpire();
        }
        return expire;
    }

    public String getConsistency() {
        if (StringUtils.isEmpty(consistency) && parent != null) {
            return parent.getConsistency();
        }
        return consistency;
    }

    public String getKeySerialName() {
        if (StringUtils.isEmpty(keySerialName) && parent != null) {
            return parent.getKeySerialName();
        }
        return keySerialName;
    }

    public String getValueWrapperSerialName() {
        if (StringUtils.isEmpty(valueWrapperSerialName) && parent != null) {
            return parent.getValueWrapperSerialName();
        }
        return valueWrapperSerialName;
    }

    public String getValueCompressSerialName() {
        if (StringUtils.isEmpty(valueCompressSerialName) && parent != null) {
            return parent.getValueCompressSerialName();
        }
        return valueCompressSerialName;
    }

    public String getValueSerialName() {
        if (StringUtils.isEmpty(valueSerialName) && parent != null) {
            return parent.getValueSerialName();
        }
        return valueSerialName;
    }

    public String getUredisValueSerialName() {
        if (StringUtils.isEmpty(uredisValueSerialName) && parent != null) {
            return parent.getUredisValueSerialName();
        }
        return uredisValueSerialName;
    }

    public int getCompressThreshold() {
        if (compressThreshold < 0 && parent != null) {
            return parent.getCompressThreshold();
        }
        return compressThreshold;
    }
}
