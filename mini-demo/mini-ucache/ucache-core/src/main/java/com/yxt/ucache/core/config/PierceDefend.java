package com.yxt.ucache.core.config;

import com.yxt.ucache.common.constants.CacheConstants;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PierceDefend {

    /**
     * 是否存null值
     */
    private boolean cacheNullValue = CacheConstants.DEFAULT_CACHE_NULL_VALUE;
    /**
     * null值存储过期时间
     */
    private Duration nullValueExpire = Duration.ofMillis(CacheConstants.NULL_VALUE_EXPIRE);
    /**
     * 穿透后加锁时间
     */
    private Duration lockTime = Duration.ofSeconds(3);

}
