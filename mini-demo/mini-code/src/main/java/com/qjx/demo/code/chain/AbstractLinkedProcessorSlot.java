package com.qjx.demo.code.chain;

/**
 * <Description>
 *
 * @author qinjiaxing on 2024/7/22
 * @author <others>
 */
public abstract class AbstractLinkedProcessorSlot<T> implements ProcessorSlot {

    private AbstractLinkedProcessorSlot<?> next = null;

    public AbstractLinkedProcessorSlot<?> getNext() {
        return next;
    }

    public void setNext(AbstractLinkedProcessorSlot<?> next) {
        this.next = next;
    }


}
