package com.yxt.ucache.core.event;

import com.yxt.ucache.core.filter.FilterContext;

public class SetGetEvent extends OpEvent {

    public SetGetEvent(FilterContext context) {
        super(context);
    }
}
