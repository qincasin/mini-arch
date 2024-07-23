package com.qjx.demo.code.chain;

/**
 * <Description>
 *
 * @author qinjiaxing on 2024/7/22
 * @author <others>
 */
public class DefaultProcessorSlotChain extends ProcessorSlotChain {

    AbstractLinkedProcessorSlot<?> first = new AbstractLinkedProcessorSlot<Object>() {
        @Override
        public AbstractLinkedProcessorSlot<?> getNext() {
            return super.getNext();
        }

        @Override
        public void setNext(AbstractLinkedProcessorSlot<?> next) {
            super.setNext(next);
        }
    };

    @Override
    public void addFirst(AbstractLinkedProcessorSlot<?> protocolProcess) {
    }

    @Override
    public void addLast(AbstractLinkedProcessorSlot<?> protocolProcess) {
    }
}
