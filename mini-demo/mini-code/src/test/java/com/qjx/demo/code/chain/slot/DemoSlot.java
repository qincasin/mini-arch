package com.qjx.demo.code.chain.slot;

import com.qjx.demo.code.chain.AbstractLinkedProcessorSlot;
import com.qjx.demo.code.chain.context.Context;
import com.qjx.demo.code.chain.node.DefaultNode;
import com.qjx.demo.code.spi.Spi;

/**
 * <Description>
 *
 * @author qinjiaxing on 2024/7/23
 * @author <others>
 */
@Spi
public class DemoSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    @Override
    public void entry(Context context, Object param) {
        System.out.println("------Entering for entry on DemoSlot------");
        System.out.println("Current context: " + context.getName());
        fireEntry(context, param);
    }

    @Override
    public void exit(Context context) {
        System.out.println("------Exiting for entry on DemoSlot------");
        System.out.println("Current context: " + context.getName());
        fireExit(context);
    }
}
