package com.yxt.ucache.core.event;

import com.yxt.ucache.core.filter.FilterContext;

public class ZSetRemoveEvent extends OpEvent {

    public ZSetRemoveEvent(FilterContext context) {
        super(context);
    }
}
