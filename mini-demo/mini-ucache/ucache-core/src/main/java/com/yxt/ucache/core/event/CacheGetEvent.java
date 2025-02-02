package com.yxt.ucache.core.event;

import com.yxt.ucache.core.filter.FilterContext;

public class CacheGetEvent extends OpEvent {

    public CacheGetEvent(FilterContext context) {
        super(context);
    }
}
