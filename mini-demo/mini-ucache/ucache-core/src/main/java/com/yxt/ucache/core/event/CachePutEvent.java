package com.yxt.ucache.core.event;

import com.yxt.ucache.core.filter.FilterContext;

public class CachePutEvent extends OpEvent {

    public CachePutEvent(FilterContext context) {
        super(context);
    }
}
