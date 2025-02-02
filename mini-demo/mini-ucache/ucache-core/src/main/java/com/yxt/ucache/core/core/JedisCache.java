package com.yxt.ucache.core.core;

import com.yxt.ucache.common.SerialPolicy;
import com.yxt.ucache.common.exception.UCacheException;
import com.yxt.ucache.core.core.config.NamespaceConfig;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Protocol;

/**
 * JedisCache
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public class JedisCache extends AbsExternalCache {

    private final Map<String, UJedisPubSub> subed = new ConcurrentHashMap<>();
    private JedisPool jedisPool;

    public JedisCache(NamespaceConfig config) {
        super(config);
        this.cacheClientName = "JedisCache";
    }

    @Override
    public byte[] doGet(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.get(key);
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public void doPut(byte[] key, byte[] value, long expireMill) {
        Jedis jedis = getJedis();
        try {
            jedis.psetex(key, expireMill, value);
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public boolean doPutIfAbsent(byte[] key, byte[] value, long expireMill) {
        Jedis jedis = getJedis();
        try {
            boolean setNx = jedis.setnx(key, value) > 0;
            if (setNx) {
                jedis.pexpire(key, expireMill);
            }
            return setNx;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    protected boolean doRemove(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.del(key) > 0;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    protected boolean doRemoveAll(byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.del(keys) > 0;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public boolean doUnlink(byte[] key) {
        Jedis jedis = getJedis();
        try {
            Long u = jedis.unlink(key);
            return u != null && u > 0;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public boolean doUnlinkAll(byte[]... keys) {
        Jedis jedis = getJedis();
        try {
            return jedis.unlink(keys) > 0;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public Boolean exists(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.exists(key);
        } catch (Exception e) {
            logger.error("UCache exists error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public Boolean expire(String key, long expireMillis) {
        Jedis jedis = getJedis();
        try {
            Long e = jedis.pexpire(key, expireMillis);
            return e != null && e > 0;
        } catch (Exception e) {
            logger.error("UCache expire error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public Boolean expire(String key, long expire, TimeUnit timeUnit) {
        return expire(key, timeUnit.toMillis(expire));
    }

    @Override
    public void rename(String key, String newKey) {
        Jedis jedis = getJedis();
        try {
            jedis.rename(key, newKey);
        } catch (Exception e) {
            logger.error("UCache rename error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public Long pttl(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.pttl(key);
        } catch (Exception e) {
            logger.error("UCache pttl error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    protected void doPub(String channel, PubSubBody msg) {
        Jedis jedis = getJedis();
        try {
            jedis.publish(keySerial.encoder().apply(channel), valueSerial.encoder().apply(msg));
        } catch (Exception e) {
            logger.error("UCache doPub error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public void sub(Consumer<PubSubBody> consumer, String... channels) {
        UJedisPubSub jedisPubSub = new UJedisPubSub(consumer, valueWrapperSerial);
        for (String channel : channels) {
            subed.put(channel, jedisPubSub);
        }
        Jedis jedis = getJedis();
        try {
            jedis.subscribe(jedisPubSub, channels);
        } catch (Exception e) {
            logger.error("UCache sub error ", e);
            throw e;
        } finally {
            releaseJedis(jedis);
        }
    }

    @Override
    public void unSub(String... channels) {
        Set<UJedisPubSub> unSubed = new HashSet<>();
        for (String channel : channels) {
            UJedisPubSub jedisPubSub = subed.remove(channel);
            if (jedisPubSub != null && !unSubed.contains(jedisPubSub)) {
                jedisPubSub.unsubscribe(channels);
                unSubed.add(jedisPubSub);
            }
        }
    }

    @Override
    public void close() throws Exception {
        try {
            super.close();
            unSub(subed.keySet().toArray(new String[0]));
            jedisPool.close();
            closed.compareAndSet(false, true);
        } catch (Exception e) {
            // ignore
            logger.warn("", e);
        }
    }

    private Jedis getJedis() {
        if (jedisPool == null) {
            initPool();
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (Exception e) {
            if (closed.get()) {
                logger.error("UCache getJedis error: ", e);
                throw new UCacheException("UCache getJedis error");
            } else {
                initPool();
            }
        }
        if (jedis == null) {
            jedis = jedisPool.getResource();
            if (jedis == null) {
                logger.error("UCache getJedis error");
                throw new UCacheException("jedis is null");
            }
        }
        return jedis;
    }

    private void releaseJedis(Jedis jedis) {
        try {
            if (jedis != null) {
                jedis.close();
            }
        } catch (Exception e) {
            logger.warn("UCache releaseJedis error ", e);
        }
    }

    private void initPool() {
        GenericObjectPoolConfig<? extends Object> poolConfig = new GenericObjectPoolConfig<>();
        NamespaceConfig.Pool pool = config.getJedis().getPool();
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxTotal(pool.getMaxActive());
        poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
        // 业务量很大时候建议设置为false，减少一次ping的开销。
        // 向资源池借用连接时是否做连接有效性检测（ping）。检测到的无效连接将会被移除。
//        poolConfig.setTestOnBorrow(true);
        // 向资源池归还连接时是否做连接有效性检测（ping）。检测到无效连接将会被移除。
//        poolConfig.setTestOnReturn(true);

        if (StringUtils.isNotEmpty(config.getPassword())) {
            int timeout = config.getTimeout() == null ? Protocol.DEFAULT_TIMEOUT : (int) config.getTimeout().toMillis();
            jedisPool = new JedisPool(poolConfig, config.getHost(), config.getPort(), timeout, config.getPassword());
        } else {
            jedisPool = new JedisPool(poolConfig, config.getHost(), config.getPort());
        }
    }

    static class UJedisPubSub extends JedisPubSub {

        Consumer<PubSubBody> consumer;
        SerialPolicy valueWrapperSerial;

        public UJedisPubSub(Consumer<PubSubBody> consumer, SerialPolicy valueWrapperSerial) {
            this.consumer = consumer;
            this.valueWrapperSerial = valueWrapperSerial;
        }

        @Override
        public void onMessage(String channel, String message) {
            consumer.accept((PubSubBody) valueWrapperSerial.decoder().apply(message.getBytes(StandardCharsets.UTF_8)));
        }
    }
}
