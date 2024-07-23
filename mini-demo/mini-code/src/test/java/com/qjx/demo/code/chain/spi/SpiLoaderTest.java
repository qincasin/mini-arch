package com.qjx.demo.code.chain.spi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import com.qjx.demo.code.chain.ProcessorSlot;
import com.qjx.demo.code.spi.SpiLoader;
import org.junit.Before;
import org.junit.Test;

/**
 * <Description>
 *
 * @author qinjiaxing on 2024/7/23
 * @author <others>
 */
public class SpiLoaderTest {

    @Before
    public void setUp() {
        SpiLoader.resetAndClearAll();
    }

    @Before
    public void tearDown() {
        SpiLoader.resetAndClearAll();
    }

    @Test
    public void testCreateSpiLoader(){
        SpiLoader slotLoader1 = SpiLoader.of(ProcessorSlot.class);
        assertNotNull(slotLoader1);
        SpiLoader slotLoader2 = SpiLoader.of(ProcessorSlot.class);
        assertSame(slotLoader2,slotLoader1);
    }


}
