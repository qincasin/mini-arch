package com.yxt.ucache.core.core.config;


import com.yxt.ucache.common.constants.CacheConstants;
import com.yxt.ucache.common.enums.CacheInternalType;
import java.time.Duration;

public class InternalConfig {
    /**
     * 开关
     */
    private boolean enabled = CacheConstants.DEFAULT_LOCAL_ENABLED;

    /**
     * 缓存类型
     */
    private String type = CacheInternalType.CAFFEINE.getVal();

    /**
     * 最大数量
     */
    private int maximumSize = CacheConstants.DEFAULT_LOCAL_LIMIT;
    /**
     * 默认过期时间
     */
    private Duration expireAfterWrite = Duration.ofMillis(CacheConstants.DEFAULT_LOCAL_EXPIRE);

    public int getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
    }

    public Duration getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public void setExpireAfterWrite(Duration expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
