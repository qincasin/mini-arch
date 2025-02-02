package com.yxt.ucache.core.event;

import com.yxt.ucache.core.filter.FilterContext;

public class CacheRemoveEvent extends OpEvent {

    public CacheRemoveEvent(FilterContext context) {
        super(context);
    }
}
