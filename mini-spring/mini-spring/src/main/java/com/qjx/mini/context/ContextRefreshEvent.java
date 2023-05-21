package com.qjx.mini.context;

/**
 * <Description>
 *
 * @author qinjiaxing on 2023/5/21
 * @author <others>
 */
public class ContextRefreshEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;
    public ContextRefreshEvent(Object source) {
        super(source);
    }

    @Override
    public String toString() {
        return this.msg;
    }
}
