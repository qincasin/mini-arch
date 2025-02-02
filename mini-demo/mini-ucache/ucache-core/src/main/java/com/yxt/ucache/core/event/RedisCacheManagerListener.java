package com.yxt.ucache.core.event;

import java.util.Collections;
import java.util.Set;

/**
 * RedisCacheManagerListener
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public class RedisCacheManagerListener extends AbsCacheOpEventListener{

    @Override
    public Set<Class<? extends OpEvent>> getEventType() {
        return Collections.emptySet();
    }

    @Override
    public void on(OpEvent event) {

    }
}
