package com.qjx.demo.code.chain;

import com.qjx.demo.code.chain.context.Context;

/**
 * <Description>
 *
 * @author qinjiaxing on 2024/7/22
 * @author <others>
 */
public abstract class AbstractLinkedProcessorSlot<T> implements ProcessorSlot {

    private AbstractLinkedProcessorSlot<?> next = null;

    public void transformEntry(Context context, Object o) {
        T t = (T) o;
        entry(context, t);
    }

    @Override
    public void fireEntry(Context context, Object param) {
        if (next != null) {
            next.transformEntry(context, param);
        }
    }

    @Override
    public void fireExit(Context context) {
        if (next != null) {
            next.exit(context);
        }
    }

    public AbstractLinkedProcessorSlot<?> getNext() {
        return next;
    }

    public void setNext(AbstractLinkedProcessorSlot<?> next) {
        this.next = next;
    }
}
