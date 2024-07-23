package com.qjx.demo.code.chain.slot;

import com.qjx.demo.code.chain.ProcessorSlotChain;
import com.qjx.demo.code.chain.SlotChainBuilder;
import com.qjx.demo.code.chain.context.Context;
import com.qjx.demo.code.spi.SpiLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <Description>
 *
 * @author qinjiaxing on 2024/7/23
 * @author <others>
 */
public class DemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        ProcessorSlotChain chain = null;
        Context context = new Context();
        context.setName("1111");
        try {
            SlotChainBuilder slotChainBuilder = SpiLoader.of(SlotChainBuilder.class).loadFirstInstanceOrDefault();
            chain = slotChainBuilder.build();
            chain.entry(context, 1);
        } catch (Exception e) {
            logger.warn("error :  ", e);
        } finally {
            if (chain != null) {
                chain.exit(context);
            }
        }
        logger.info("end");
    }

}
