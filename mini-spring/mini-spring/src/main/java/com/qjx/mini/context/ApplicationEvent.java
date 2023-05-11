package com.qjx.mini.context;

import java.util.EventObject;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/10
 * @author <others>
 */
public class ApplicationEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    public ApplicationEvent(Object source) {
        super(source);
    }
}
