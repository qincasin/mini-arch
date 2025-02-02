package com.yxt.ucache.core.event;

import com.yxt.ucache.core.filter.FilterContext;

public class CacheAllEvent extends OpEvent {

    protected CacheAllEvent(FilterContext context) {
        super(context);
    }
}
