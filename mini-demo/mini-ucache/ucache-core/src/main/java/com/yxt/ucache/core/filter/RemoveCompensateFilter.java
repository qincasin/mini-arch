package com.yxt.ucache.core.filter;

import com.google.common.base.Objects;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yxt.ucache.common.CacheProxy;
import com.yxt.ucache.core.utils.ULRUMap;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 删除补偿filter, 具体策略由子类实现
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public class RemoveCompensateFilter extends AbsInvokeFilter {

    protected ULRUMap<RemovedKey, Object> keyQueue;
    private ScheduledThreadPoolExecutor scheduled;
    /**
     * 队列数量限制，限制最大容纳的key信息
     */
    private int maxSize = 2000;
    /**
     * 最大重试次数
     */
    private int maxTimes = 3;
    /**
     * 重试间隔时间, 单位秒
     */
    private int delaySeconds = 3;
    /**
     * 重试时间膨胀比例，
     * 默认
     * 第一次间隔3s
     * 第二次间隔5s
     * 第三次间隔8s
     */
    private double dilationRate = 1.5;

    private Object val = new Object();

    public RemoveCompensateFilter(int maxSize, int maxTimes, int delaySeconds, String name, String namespace) {
        super(name, namespace, null);
        this.maxSize = maxSize;
        this.maxTimes = maxTimes;
        this.delaySeconds = delaySeconds;
        scheduled = new ScheduledThreadPoolExecutor(2, new ThreadFactoryBuilder().setDaemon(true).setNameFormat(name + "-").build());
        keyQueue = new ULRUMap<>(maxSize);
        start();
    }

    private void start() {
        scheduled.scheduleWithFixedDelay(() -> {
            try {
                for (Entry<RemovedKey, Object> entry : keyQueue.getAll()) {
                    doRemove(entry);
                }
            } catch (Exception ex) {
                logger.warn("UCache key 删除补偿 再次删除任务执行失败 ", ex);
            }
        }, delaySeconds, delaySeconds, TimeUnit.SECONDS);
    }

    public void addTask(RemovedKey removedKey) {
        if (keyQueue.size() > maxSize && !keyQueue.containsKey(removedKey)) {
            logger.warn("UCache key 删除补偿, {}, 待删除队列已满 {}", removedKey, keyQueue.size());
            return;
        }
        logger.warn("UCache key 删除补偿, 等待重试; {}", removedKey);
        keyQueue.put(removedKey, val);
    }

    private void doRemove(Entry<RemovedKey, Object> entry) {
        RemovedKey removedKey = entry.getKey();
        long now = System.currentTimeMillis();
        try {
            if (removedKey.times >= maxTimes) {
                logger.warn("UCache key 删除补偿 {}, 已重试最大次数 {}", removedKey, removedKey.times);
                keyQueue.remove(removedKey);
                return;
            }
            if ((now - removedKey.lastTimestamp) < Math.pow(dilationRate, removedKey.times) * delaySeconds) {
                // 时间间隔不够，下次再试
                return;
            }
            CacheProxy target = getTarget().unProxy();
            if (removedKey.keyObj instanceof String) {
                target.remove(removedKey.keyObj.toString());
            }
            if (removedKey.keyObj instanceof Set) {
                target.removeAll((Set) removedKey.keyObj);
            }
            keyQueue.remove(removedKey);
        } catch (Exception e) {
            logger.warn("UCache key 删除补偿 {}, 重试也失败, 等待下次重试 {}", removedKey, e);
            removedKey.times++;
            removedKey.lastTimestamp = now;
        }
    }

    @Override
    public Object invoke(FilterContext context) {
        return super.invoke(context);
    }

    @Override
    protected boolean canProcess(FilterContext context) {
        return false;
    }

    public static class RemovedKey {

        // key 对象，可能是String也可能是Set
        Object keyObj;
        // 已重试次数
        int times;
        // 初始时间
        long initTimestamp;
        // 最后更新时间
        long lastTimestamp;

        RemovedKey(Object keyObj) {
            this.keyObj = keyObj;
            initTimestamp = System.currentTimeMillis();
            lastTimestamp = initTimestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof RemovedKey)) {
                return false;
            }
            RemovedKey that = (RemovedKey) o;
            return Objects.equal(keyObj, that.keyObj);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(keyObj);
        }

        public long getInitTimestamp() {
            return initTimestamp;
        }

        public void setInitTimestamp(long initTimestamp) {
            this.initTimestamp = initTimestamp;
        }

        public Object getKeyObj() {
            return keyObj;
        }

        public void setKeyObj(Object keyObj) {
            this.keyObj = keyObj;
        }

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }

        public long getLastTimestamp() {
            return lastTimestamp;
        }

        public void setLastTimestamp(long lastTimestamp) {
            this.lastTimestamp = lastTimestamp;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (keyObj instanceof String) {
                sb.append("key=").append(keyObj);
            } else if (keyObj instanceof Set) {
                sb.append("keys=").append(Arrays.toString(((Set) keyObj).toArray()));
            } else {
                sb.append("key=").append(keyObj);
            }
            return sb.toString();
        }
    }
}
