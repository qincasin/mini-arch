package com.yxt.ucache.core.core;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.yxt.ucache.common.CacheClient;
import com.yxt.ucache.common.ValWrapper;
import com.yxt.ucache.core.core.config.InternalConfig;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * <Description>
 *
 * @author qinjiaxing on 2025/2/2
 * @author <others>
 */
public class CaffeineCacheBuilder extends InternalCacheBuilder {

    private InternalConfig caffeineConfig;

    public CaffeineCacheBuilder(InternalConfig internalConfig) {
        super(null, internalConfig);
        this.maximumSize = internalConfig.getMaximumSize();
        this.expireAfterWrite = internalConfig.getExpireAfterWrite();
    }

    public static CaffeineCacheBuilder createBuilder(InternalConfig internalConfig) {
        return new CaffeineCacheBuilder(internalConfig);
    }

    @Override
    public CacheClient buildCache() {
        caffeineConfig.setMaximumSize(this.maximumSize);
        caffeineConfig.setExpireAfterWrite(this.expireAfterWrite);
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        builder.maximumSize(caffeineConfig.getMaximumSize());
        // 自定义单个key过期时间配置
        builder.expireAfter(new Expiry<Object, ValWrapper>() {
            private long getRestTimeInNanos(ValWrapper value) {
                long ttl = Math.min(caffeineConfig.getExpireAfterWrite().toMillis(), value.getExpire());
                if (ttl < 0) {
                    return 0;
                }
                return TimeUnit.MILLISECONDS.toNanos(ttl);
            }

            @Override
            public long expireAfterCreate(@NonNull Object key, @NonNull ValWrapper value, long currentTime) {
                return getRestTimeInNanos(value);
            }

            @Override
            public long expireAfterUpdate(@NonNull Object key, @NonNull ValWrapper valWrapper, long currentTime, @NonNegative long currentDuration) {
                return currentDuration;
            }

            @Override
            public long expireAfterRead(@NonNull Object key, @NonNull ValWrapper valWrapper, long currentTime, @NonNegative long currentDuration) {
                return getRestTimeInNanos(valWrapper);
            }
        });
        CaffeineCache cache = new CaffeineCache(builder.build(), caffeineConfig);
        addTailFilter(cache);
        return createCacheProxy(cache);
    }

}
