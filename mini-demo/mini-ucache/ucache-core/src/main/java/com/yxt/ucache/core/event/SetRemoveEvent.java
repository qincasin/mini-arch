package com.yxt.ucache.core.event;

import com.yxt.ucache.core.filter.FilterContext;

public class SetRemoveEvent extends OpEvent {

    public SetRemoveEvent(FilterContext context) {
        super(context);
    }
}
