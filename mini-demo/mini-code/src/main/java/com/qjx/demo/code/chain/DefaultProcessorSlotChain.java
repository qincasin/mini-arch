package com.qjx.demo.code.chain;

import com.qjx.demo.code.chain.context.Context;

/**
 * <Description>
 *
 * @author qinjiaxing on 2024/7/22
 * @author <others>
 */
public class DefaultProcessorSlotChain extends ProcessorSlotChain {

    AbstractLinkedProcessorSlot<?> first = new AbstractLinkedProcessorSlot<Object>() {
        @Override
        public void entry(Context context, Object param) {
            super.fireEntry(context, param);
        }

        @Override
        public void exit(Context context) {
            super.fireExit(context);
        }
    };
    AbstractLinkedProcessorSlot<?> end = first;

    @Override
    public void addFirst(AbstractLinkedProcessorSlot<?> protocolProcess) {
        protocolProcess.setNext(first.getNext());
        first.setNext(protocolProcess);
        if (end == first) {
            end = protocolProcess;
        }
    }

    @Override
    public void addLast(AbstractLinkedProcessorSlot<?> protocolProcess) {
        end.setNext(protocolProcess);
        end = protocolProcess;
    }

    @Override
    public AbstractLinkedProcessorSlot<?> getNext() {
        return first.getNext();
    }

    @Override
    public void setNext(AbstractLinkedProcessorSlot<?> next) {
        addLast(next);
    }

    @Override
    public void entry(Context context, Object param) {
        first.transformEntry(context, param);
    }

    @Override
    public void exit(Context context) {
        first.exit(context);
    }
}
