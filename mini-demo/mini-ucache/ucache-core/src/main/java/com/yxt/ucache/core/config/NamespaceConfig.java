package com.yxt.ucache.core.config;

import com.yxt.ucache.common.constants.CacheConstants;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 缓存实例配置 用于创建一个缓存实例, 也就是 Cache 对象
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
@Getter
@Setter
public class NamespaceConfig extends InheritableConfig<UCacheConfig> {

    private final Jedis jedis = new Jedis();
    private final Lettuce lettuce = new Lettuce();
    /**
     * 当前namespace名
     */
    private String namespace = CacheConstants.DEFAULT_NAMESPACE;
    /**
     * cacheName配置
     */
    private Map<String, CacheNameConfig> cacheName = new HashMap<>();
    /**
     * 缓存组件类型
     */
    private String type;
    private int database = 0;
    private String url;
    private String host;
    private String password;
    private int port = 6379;
    private boolean ssl;
    private Duration timeout;
    private String clientName;
    private Sentinel sentinel;
    private Cluster cluster;

    public boolean isCacheNullValue(String cacheName) {
        // 全局开关 全局为关 则关闭
        boolean globalNullValue = this.getParent().getPierceDefend().isCacheNullValue();
        if (!globalNullValue) {
            return false;
        }
        CacheNameConfig cacheNameConfig = this.getCacheName().get(cacheName);
        return cacheNameConfig == null || cacheNameConfig.getCacheNullValue();
    }


    public Duration getNullValueExpire(String cacheName) {
        CacheNameConfig cacheNameConfig = this.getCacheName().get(cacheName);
        Duration cacheNullValueExpire;
        if (cacheNameConfig == null) {
            cacheNullValueExpire = this.getParent().getPierceDefend().getNullValueExpire();
        } else {
            cacheNullValueExpire = cacheNameConfig.getNullValueExpire();
        }
        return cacheNullValueExpire;
    }

    public Duration getLockTime(String cacheName) {
        CacheNameConfig cacheNameConfig = this.getCacheName().get(cacheName);
        Duration lockTime;
        if (cacheNameConfig == null) {
            lockTime = this.getParent().getPierceDefend().getLockTime();
        } else {
            lockTime = cacheNameConfig.getLockTime();
        }
        return lockTime;
    }

    @Setter
    @Getter
    public static class Pool {

        private int maxIdle = 8;

        private int minIdle = 0;

        private int maxActive = 8;

        private Duration maxWait = Duration.ofMillis(-1);

        private Duration timeBetweenEvictionRuns;
    }

    @Setter
    @Getter
    public static class Cluster {

        private List<String> nodes;

        private Integer maxRedirects;

    }

    @Setter
    @Getter
    public static class Sentinel {

        private String master;

        private List<String> nodes;

        private String password;

    }

    @Setter
    @Getter
    public static class Jedis {

        private Pool pool;

    }


    @Data
    public static class Lettuce {

        private final Cluster cluster = new Cluster();
        private Duration shutdownTimeout = Duration.ofMillis(100);
        private Pool pool;

        @Setter
        @Getter
        public static class Cluster {

            private final Refresh refresh = new Refresh();

            @Getter
            @Setter
            public static class Refresh {

                /**
                 * Cluster topology refresh period.
                 */
                private Duration period;

                /**
                 * Whether adaptive topology refreshing using all available refresh
                 * triggers should be used.
                 */
                private boolean adaptive;
            }
        }

    }
}
