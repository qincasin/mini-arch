package com.yxt.ucache.core.core;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yxt.ucache.common.CacheLock;
import com.yxt.ucache.common.SerialGenericPolicy;
import com.yxt.ucache.common.SerialPolicy;
import com.yxt.ucache.common.ValWrapper;
import com.yxt.ucache.common.constants.CacheConstants;
import com.yxt.ucache.common.enums.CacheType;
import com.yxt.ucache.common.uredis.URedisClient;
import com.yxt.ucache.core.core.config.NamespaceConfig;
import com.yxt.ucache.core.core.config.UCacheConfig;
import com.yxt.ucache.core.event.PubSub;
import com.yxt.ucache.core.exception.CacheInterruptException;
import com.yxt.ucache.core.utils.BatchUtils;
import com.yxt.ucache.core.utils.InnerAssertUtils;
import com.yxt.ucache.core.utils.InnerCodecUtils;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 外部缓存实例, 需要序列化
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
public abstract class AbsExternalCache extends AbsCache implements PubSub, URedisClient {

    protected final SerialPolicy keySerial;
    protected final SerialPolicy valueWrapperSerial;
    protected final SerialPolicy valueCompressSerial;
    protected final SerialPolicy valueSerial;
    protected final SerialGenericPolicy uredisValueSerial;

    // 初始化方法cas
    protected final AtomicBoolean init = new AtomicBoolean(false);
    private final AtomicBoolean subSuccess = new AtomicBoolean(false);

    private final Map<String,Set<String>> batchPubQueue = new ConcurrentHashMap<>();

    protected Map<String, Consumer<PubSubBody>> consumerMap = new ConcurrentHashMap<>();
    protected static final ScheduledThreadPoolExecutor batchPubScheduled;


    protected AbsExternalCache(NamespaceConfig config) {
        super(config, CacheType.REMOTE);
        keySerial = UCacheManager.getSerialPolicy(config.getKeySerialName(), UCacheManager.getSerialPolicy(SerialPolicy.Jackson, null));
        valueWrapperSerial = UCacheManager.getSerialPolicy(config.getValueWrapperSerialName(), UCacheManager.getSerialPolicy(SerialPolicy.Jackson, null));
        valueCompressSerial = UCacheManager.getSerialPolicy(config.getValueCompressSerialName(), UCacheManager.getSerialPolicy(SerialPolicy.Gzip, null));
        valueSerial = UCacheManager.getSerialPolicy(config.getValueSerialName(), UCacheManager.getSerialPolicy(SerialPolicy.Jackson, null));
        uredisValueSerial = (SerialGenericPolicy) valueSerial;
        this.cacheLock = new RedisCacheLock(this);
    }

    static {
        batchPubScheduled = new ScheduledThreadPoolExecutor(1,new ThreadFactoryBuilder().setDaemon(true).setNameFormat("batchPubScheduled-").build());
    }

    public void addRemoveConsumer(Consumer<PubSubBody> consumer) {
        logger.debug("UCache AbsExternalCache 添加1名订阅者, 监听 '{}'", PubSub.TypeRemoveKey);
        consumerMap.put(keyRemoveTopic, consumer);
    }

    @Override
    public void init() {
        init.compareAndSet(false, true);
        // 启动sub
        startSub();
        // 启动批量pub任务
        startBatchPubTask();
    }


    @Override
    public void close() throws Exception {
        try {
            if (batchPubScheduled != null) {
                batchPubScheduled.awaitTermination(1500, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            // ignore
            logger.warn("", e);
        }
    }

    @Override
    public Object serialVal(ValWrapper valWrapper) {
        return InnerCodecUtils.serialVal(valWrapper, this.valueSerial, this.valueCompressSerial, config.getCompressThreshold());
    }

    @Override
    public Object deSerialVal(ValWrapper valWrapper) {
        return InnerCodecUtils.serialVal(valWrapper, this.valueSerial, this.valueCompressSerial, config.getCompressThreshold());
    }

    @Override
    public ValWrapper get(String key) {
        byte[] val = null;
        try {
            val = doGet(InnerCodecUtils.encode(keySerial, key));
        } catch (Exception e) {
            logger.error("UCache doGet error, key={} ", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_NET,
                    "doGet error, key=" + key + e.getMessage());
        }
        if (val == null) {
            return null;
        }
        ValWrapper valWrapper = (ValWrapper) InnerCodecUtils.decode(valueWrapperSerial, val);
        valWrapper.setValue(deSerialVal(valWrapper));
        return valWrapper;
    }


    public abstract byte[] doGet(byte[] key);

    @Override
    public <K, V> void put(String key, ValWrapper valWrapper) {
        InnerAssertUtils.notNull(valWrapper, "'valWrapper' can not be null");
        try {
            valWrapper.setValue(serialVal(valWrapper));
            long expireMill = expireBy(valWrapper);
            doPut(InnerCodecUtils.encode(keySerial, key), InnerCodecUtils.encode(valueSerial, valWrapper), expireMill);
        } catch (Exception e) {
            logger.error("UCache doPut error, key={} ", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_NET,
                    "doPut error, key=" + key + e.getMessage());
        }
    }
    public abstract void doPut(byte[] key, byte[] value, long expireMill);

    @Override
    public <K, V> boolean putIfAbsent(String key, ValWrapper valWrapper) {
        InnerAssertUtils.notNull(valWrapper, "'valWrapper' can not be null");
        try {
            valWrapper.setValue(serialVal(valWrapper));
            long expireMill = expireBy(valWrapper);
            return this.doPutIfAbsent(InnerCodecUtils.encode(keySerial, key), InnerCodecUtils.encode(valueSerial, valWrapper), expireMill);
        } catch (Exception e) {
            logger.error("UCache doPutIfAbsent error, key={} ", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_NET,
                    "doPutIfAbsent error, key=" + key + e.getMessage());
        }
    }

    public abstract boolean doPutIfAbsent(byte[] key, byte[] value, long expireMill);


    @Override
    public <K> boolean remove(String key) {
        try {
            return this.doRemove(InnerCodecUtils.encode(keySerial, key));
        } catch (Exception e) {
            logger.error("UCache remove error,key={} ", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_NET, "remove error, key=" + key + e.getMessage());
        }finally {
            // 如果不是以 _uclock 结尾的 则发布一次pub
            if (!key.startsWith(CacheLock.LOCK_POSTFIX)) {
                PubSubBody body = new PubSubBody();
                body.getKeys().add(key);
                body.setChannel(keyRemoveTopic);
                pub(keyRemoveTopic, body);
            }
        }
    }

    protected abstract boolean doRemove(byte[] key);

    @Override
    public <K> boolean removeAll(Set<String> keys) {
        byte[][] keyArray = new byte[keys.size()][];
        int i = 0;
        for (String key : keys) {
            keyArray[i++] = InnerCodecUtils.encode(keySerial, key);
        }
        try {
            int batchSize = config.getParent().getBatchSize();
            if (keyArray.length > batchSize) {
                logger.warn("UCache removeAll 单词操作数量太多 {}", keys.size());
                return BatchUtils.batchList(batchSize, Lists.newArrayList(keyArray), e -> {
                    return this.doRemoveAll(e.toArray(new byte[0][]));
                });
            }
            return this.doRemoveAll(keyArray);
        } catch (Exception e) {
            logger.error("UCache removeAll error,keys={} ", keys, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_NET,
                    "doRemoveAll error, key=" + keys + e.getMessage());
        } finally {
            PubSubBody body = new PubSubBody();
            body.getKeys().addAll(keys);
            body.setChannel(keyRemoveTopic);
            pub(keyRemoveTopic, body);
        }
    }

    protected abstract boolean doRemoveAll(byte[]... keys);

    @Override
    public Boolean unlink(String key) {
        try {
            return this.doUnlink(InnerCodecUtils.encode(keySerial, key));
        } catch (Exception e) {
            logger.error("UCache unlink error, key={} ", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_NET,
                    "unlink error, key=" + key + e.getMessage());
        }finally {
            PubSubBody body = new PubSubBody();
            body.getKeys().add(key);
            body.setChannel(keyRemoveTopic);
            pub(keyRemoveTopic, body);
        }
    }

    public abstract boolean doUnlink(byte[] key);

    @Override
    public Boolean unlinkAll(Set<String> keys) {
        byte[][] keyArray = new byte[keys.size()][];
        int i = 0;
        for (String key : keys) {
            keyArray[i++] = InnerCodecUtils.encode(keySerial, key);
        }
        try {
            int batchSize = config.getParent().getBatchSize();
            if (keyArray.length > batchSize) {
                logger.warn("UCache unlinkAll 单次操作数量太多 {}", keys.size());
                return BatchUtils.batchList(batchSize, Lists.newArrayList(keyArray), e -> {
                    return this.doUnlinkAll(e.toArray(new byte[0][]));
                });
            }
            return this.doUnlinkAll(keyArray);
        } catch (Exception e) {
            logger.error("UCache unlinkAll error, key={} ", keys, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_NET,
                    "unlinkAll error, key=" + keys + e.getMessage());
        } finally {
            PubSubBody body = new PubSubBody();
            body.getKeys().addAll(keys);
            body.setChannel(keyRemoveTopic);
            pub(keyRemoveTopic, body);
        }
    }
    public abstract boolean doUnlinkAll(byte[]... keys);

    /**
     * pub操作进行优化, 可以通过合并批量pub来提升性能
     * @param channel
     * @param msg
     */
    @Override
    public void pub(String channel, PubSubBody msg) {
        UCacheConfig cacheConfig = config.getParent();
        // pub sub 不开启 || 本地缓存未开启 || sub 初始化失败 ，这些情况都不处理pub
        if (!cacheConfig.isEnabledPubsub() || !cacheConfig.getLocal().isEnabled() || !subSuccess.get()) {
            return;
        }
        AtomicInteger waitPubSize = new AtomicInteger();
        if (!batchPubQueue.isEmpty()) {
            batchPubQueue.forEach((channelType, keys) -> waitPubSize.addAndGet(keys.size()));
        }
        int batchSize = cacheConfig.getBatchSize();
        if (waitPubSize.get() >= batchSize || msg.getKeys().size() >= batchSize) {
            // 待发送队列有点大, 直接pub
            doPub(channel, msg);
        } else if (!cacheConfig.isBatchPub()) {
            // 指定了直接pub
            doPub(channel, msg);
        } else {
            Set<String> keys = batchPubQueue.computeIfAbsent(msg.getChannel() + channelTypeSplit + msg.getType(), k -> new CopyOnWriteArraySet<>());
            keys.addAll(msg.getKeys());
        }
    }

    protected abstract void doPub(String channel, PubSubBody msg);


    @Override
    public Boolean del(String key) {
        try {
            // 优先使用unlick
            if (this.unlink(key)) {
                return true;
            }
        } catch (Exception e) {
            logger.warn("UCache unlink warn ", e);
            // NOSONAR
        }
        return remove(key);
    }

    @Override
    public Boolean delAll(Set<String> keys) {
        try {
            // 优先使用unlick
            if (this.unlinkAll(keys)) {
                return true;
            }
        } catch (Exception e) {
            logger.warn("UCache unlinkAll warn ", e);
            // NOSONAR
        }
        return removeAll(keys);
    }

    private long expireBy(ValWrapper valWrapper){
        long expireMill = valWrapper.getExpireTs() - System.currentTimeMillis();
        if (expireMill > 0) {
            return expireMill;
        }
        // 如果过期时间为负值或0，则返回最低有效值10ms
        return 10L;
    }

    private void startBatchPubTask() {
        batchPubScheduled.scheduleAtFixedRate(() -> {
            if (batchPubQueue.isEmpty() || closed.get()) {
                return;
            }
            UCacheConfig cacheConfig = config.getParent();
            if (!cacheConfig.isEnabledPubsub() || !cacheConfig.getLocal().isEnabled() || !subSuccess.get()) {
                batchPubQueue.clear();
                return;
            }
            for (String channelType : batchPubQueue.keySet()) {
                Set<String> keys = batchPubQueue.remove(channelType);
                PubSubBody msg = new PubSubBody();
                String[] split = channelType.split(channelTypeSplit);
                msg.setType(Integer.parseInt(split[1]));
                msg.setChannel(split[0]);
                msg.getKeys().addAll(keys);
                doPub(msg.getChannel(), msg);
            }
        }, config.getParent().getBatchPubTime().toMillis(), config.getParent().getBatchPubTime().toMillis(), TimeUnit.MILLISECONDS);
    }

    private void startSub() {
        if (!config.getParent().isEnabledPubsub() || !config.getParent().getLocal().isEnabled()) {
            return;
        }
        logger.debug("UCache AbsExternalCache 开始订阅");
        // 异步订阅
        Thread thread = subscribeThread(() -> {
            // 线程内的 run

            try {
                // 此时认为订阅成功，因为真正的订阅会“阻塞”到 this.sub() 方法上，订阅执行失败后会从该方法上返回
                subSuccess.set(true);
                this.sub(consumerBody -> {
                    try {
                        consumerMap.forEach((channel, consumerAction) -> consumerAction.accept(consumerBody));
                        // 防止线程无休止执行
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (Throwable throwable) {
                        logger.warn("subscribeThread sub的回调 warn ", throwable);
                    }
                }, keyRemoveTopic);
            } catch (Throwable throwable) {
                logger.warn("pub订阅异常 ", throwable);
            } finally {
                // 正常情况，订阅线程永远不会结束，除非线程执行异常，或者订阅异常
                subSuccess.set(false);
            }
        });
        thread.start();
    }

    protected Thread subscribeThread(Runnable runnable) {
        return new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ucache-redis-subscribe-" + getCacheInfo()).build().newThread(runnable);
    }

    @Override
    public URedisClient getURedisClient() {
        return (URedisClient) this.proxy;
    }
}
