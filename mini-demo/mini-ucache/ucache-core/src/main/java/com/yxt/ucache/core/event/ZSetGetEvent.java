package com.yxt.ucache.core.event;

import com.yxt.ucache.core.filter.FilterContext;

public class ZSetGetEvent extends OpEvent {

    public ZSetGetEvent(FilterContext context) {
        super(context);
    }
}
