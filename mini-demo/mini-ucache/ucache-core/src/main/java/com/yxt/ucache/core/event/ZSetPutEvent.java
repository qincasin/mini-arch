package com.yxt.ucache.core.event;

import com.yxt.ucache.core.filter.FilterContext;

public class ZSetPutEvent extends OpEvent {

    public ZSetPutEvent(FilterContext context) {
        super(context);
    }
}
