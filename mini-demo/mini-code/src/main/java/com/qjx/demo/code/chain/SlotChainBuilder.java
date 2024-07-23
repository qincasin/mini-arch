package com.qjx.demo.code.chain;

/**
 * The builder for processor slot chain.
 *
 * @author qinjiaxing on 2024/7/22
 * @author <others>
 */
public interface SlotChainBuilder {

    /**
     * Build the processor slot chain.
     *
     * @return a processor slot that chain some slots together
     */
    ProcessorSlotChain build();

}
