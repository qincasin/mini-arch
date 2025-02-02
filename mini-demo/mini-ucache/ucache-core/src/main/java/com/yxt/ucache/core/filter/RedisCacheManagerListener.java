package com.yxt.ucache.core.filter;

import com.google.common.collect.Sets;
import com.yxt.ucache.core.core.AbsCache;
import com.yxt.ucache.core.core.config.NamespaceConfig;
import com.yxt.ucache.core.event.AbsCacheOpEventListener;
import com.yxt.ucache.core.event.CachePutEvent;
import com.yxt.ucache.core.event.CacheRemoveEvent;
import com.yxt.ucache.core.event.OpEvent;
import com.yxt.ucache.core.event.SetPutEvent;
import com.yxt.ucache.core.event.SetRemoveEvent;
import com.yxt.ucache.core.event.ZSetPutEvent;
import com.yxt.ucache.core.event.ZSetRemoveEvent;
import java.util.Set;

public class RedisCacheManagerListener extends AbsCacheOpEventListener {
    private NamespaceConfig getRedisConfig(AbsCache cache) {
        return cache.getConfig();
    }

    @Override
    public Set<Class<? extends OpEvent>> getEventType() {
        return Sets.newHashSet(
                CachePutEvent.class, CacheRemoveEvent.class,
                SetPutEvent.class, SetRemoveEvent.class,
                ZSetPutEvent.class, ZSetRemoveEvent.class);
    }

    @Override
    public void on(OpEvent event) {
        System.out.println("收到事件 " + event.toString());
    }
}
