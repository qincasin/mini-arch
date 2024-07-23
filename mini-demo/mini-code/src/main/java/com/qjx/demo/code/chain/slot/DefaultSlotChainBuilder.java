package com.qjx.demo.code.chain.slot;

import com.qjx.demo.code.chain.AbstractLinkedProcessorSlot;
import com.qjx.demo.code.chain.DefaultProcessorSlotChain;
import com.qjx.demo.code.chain.ProcessorSlot;
import com.qjx.demo.code.chain.ProcessorSlotChain;
import com.qjx.demo.code.chain.SlotChainBuilder;
import com.qjx.demo.code.spi.Spi;
import com.qjx.demo.code.spi.SpiLoader;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <Description>
 *
 * @author qinjiaxing on 2024/7/22
 * @author <others>
 */
@Spi(isDefault = true)
public class DefaultSlotChainBuilder implements SlotChainBuilder {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ProcessorSlotChain build() {
        ProcessorSlotChain chain = new DefaultProcessorSlotChain();
        List<ProcessorSlot> sortedSlotLit = SpiLoader.of(ProcessorSlot.class).loadInstanceListSorted();
        for (ProcessorSlot slot : sortedSlotLit) {
            if (!(slot instanceof AbstractLinkedProcessorSlot)) {
                logger.warn(
                        "The ProcessorSlot(" + slot.getClass().getCanonicalName() + ") is not an instance of AbstractLinkedProcessorSlot, can't be added into ProcessorSlotChain");
                continue;
            }
            chain.addLast((AbstractLinkedProcessorSlot<?>) slot);
        }
        return chain;
    }
}
