package com.qjx.demo.code.chain;

/**
 * <Description>
 *
 * @author qinjiaxing on 2024/7/22
 * @author <others>
 */
public abstract class ProcessorSlotChain extends AbstractLinkedProcessorSlot<Object> {

    public abstract void addFirst(AbstractLinkedProcessorSlot<?> protocolProcess);

    public abstract void addLast(AbstractLinkedProcessorSlot<?> protocolProcess);
}
