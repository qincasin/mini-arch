package com.yxt.ucache.core.event;

import java.util.Set;

/**
 * AbsCacheOpEventListener
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public abstract class AbsCacheOpEventListener implements EventListener {

    public abstract Set<Class<? extends OpEvent>> getEventType();

    public boolean async() {
        return true;
    }

}
