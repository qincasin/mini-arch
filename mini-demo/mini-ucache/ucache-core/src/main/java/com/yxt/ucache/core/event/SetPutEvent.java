package com.yxt.ucache.core.event;

import com.yxt.ucache.core.filter.FilterContext;

public class SetPutEvent extends OpEvent {

    public SetPutEvent(FilterContext context) {
        super(context);
    }
}
