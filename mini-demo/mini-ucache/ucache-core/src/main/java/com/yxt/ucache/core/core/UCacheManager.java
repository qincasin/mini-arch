package com.yxt.ucache.core.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yxt.ucache.common.SerialPolicy;
import com.yxt.ucache.common.ValWrapper;
import com.yxt.ucache.core.GZIPSerializerWrapper;
import com.yxt.ucache.core.StringSerializerWrapper;
import com.yxt.ucache.core.event.EventListener;
import com.yxt.ucache.core.filter.AbsInvokeFilter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UCacheManager
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
public final class UCacheManager {

    private static Logger logger = LoggerFactory.getLogger(UCacheManager.class);
    private static Map<String, SerialPolicy> serialPolicyMap = new ConcurrentHashMap<>();
    private static List<EventListener> listeners = new CopyOnWriteArrayList<>();
    private static List<AbsInvokeFilter> allFilterList = new CopyOnWriteArrayList<>();

    /**
     * 初始化序列化选择器
     */
    static {
        serialPolicyMap.put(SerialPolicy.Gzip, new GZIPSerializerWrapper());
        serialPolicyMap.put(SerialPolicy.Jackson, new JacksonSerializer());
        serialPolicyMap.put(SerialPolicy.STRING, new StringSerializerWrapper(StandardCharsets.UTF_8));
        serialPolicyMap.put(SerialPolicy.Protostuff, new ProtostuffSerial(ValWrapper.class));
    }

    /**
     * 释放钩子
     */
    static {
        Thread thread = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("UCacheManager-release-hook")
                .build()
                .newThread(() -> {
                    try {
                        close();
                    } catch (Exception ex) {
                        logger.error("UCacheManager-release-hook error ", ex);
                    }
                });
        Runtime.getRuntime().addShutdownHook(thread);
    }

    private UCacheManager() {
    }

    public static List<EventListener> getEventListeners() {
        return listeners;
    }

    public static void addEventListener(EventListener listener) {
        if (listener == null) {
            return;
        }
        listeners.add(listener);
    }


    public static void addSerialPolicy(String name, SerialPolicy policy) {
        if (StringUtils.isEmpty(name) || policy == null) {
            return;
        }
        serialPolicyMap.put(name, policy);
    }

    /**
     * 获取序列化选择器
     *
     * @param name            #SerialPolicy.name()
     * @param defaultIfAbsent SerialPolicy
     * @return SerialPolicy
     */
    public static SerialPolicy getSerialPolicy(String name, SerialPolicy defaultIfAbsent) {
        if (StringUtils.isEmpty(name)) {
            return defaultIfAbsent;
        }
        return serialPolicyMap.getOrDefault(name, defaultIfAbsent);
    }

    public static void addFilter(AbsInvokeFilter filter) {
        allFilterList.add(filter);
    }

    private static void close() {
        serialPolicyMap.clear();
        listeners.clear();
        allFilterList.clear();
    }

}
