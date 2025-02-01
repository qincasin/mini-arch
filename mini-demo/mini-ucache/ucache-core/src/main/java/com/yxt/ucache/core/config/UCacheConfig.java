package com.yxt.ucache.core.config;

import com.yxt.ucache.common.SerialPolicy;
import com.yxt.ucache.common.constants.CacheConstants;
import com.yxt.ucache.common.enums.ConsistencyType;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * UCacheConfig
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
@Getter
@Setter
public class UCacheConfig extends InheritableConfig<UCacheConfig> {

    /**
     * 全局开关
     */
    private boolean enabled = CacheConstants.DEFAULT_ENABLED;
    /**
     * 应用名
     */
    private String appName = CacheConstants.DEFAULT;
    /**
     * 数据表示
     */
    private String trafficTag;
    /**
     * 默认过期时间
     */
    private Duration expire = Duration.ofMillis(CacheConstants.DEFAULT_EXPIRE);
    /**
     * 击穿防护
     */
    private BreakdownDefend breakdownDefend = new BreakdownDefend();
    /**
     * 穿透防护
     */
    private PierceDefend pierceDefend = new PierceDefend();
    /**
     * 一致性策略
     */
    private String consistency = ConsistencyType.EVENTUAL.getVal();
    /**
     * debug调试日志
     */
    private boolean debug = false;
    /**
     * 开启Pubsub功能
     */
    private boolean enabledPubsub = true;
    /**
     * 批量Pub优化
     */
    private boolean batchPub = true;
    /**
     * 批量处理数量
     */
    private int batchSize = 500;
    /**
     * 批量Pub时间, 单位毫秒
     */
    private Duration batchPubTime = Duration.ofMillis(200);

    /**
     * 大缓存配置
     */
    private BigKeyConfig bigKey = new BigKeyConfig();
    /**
     * 缓存降级
     */
    private CircuitBreakerConfig circuitBreaker = new CircuitBreakerConfig();

    /**
     * 远程缓存配置
     */
    private Map<String, NamespaceConfig> remote = new HashMap<>();
    /**
     * 本地缓存配置
     */
    private InternalConfig local = new InternalConfig();

    /**
     * 缓存key 序列化方式
     */
    private String keySerialName = SerialPolicy.STRING;
    /**
     * 缓存包装类 压缩方式
     */
    private String valueWrapperSerialName = SerialPolicy.Jackson;
    /**
     * 缓存存储值 压缩方式
     */
    private String valueCompressSerialName = SerialPolicy.Gzip;
    /**
     * 缓存存储值 序列化方式
     */
    private String valueSerialName = SerialPolicy.Jackson;
    /**
     * 压缩阈值, 单位字节
     */
    private int compressThreshold = CacheConstants.COMPRESS_THRESHOLD;

    /**
     * 导出相关配置
     */
    private CacheReporterConfig cacheReporter = new CacheReporterConfig();

}
