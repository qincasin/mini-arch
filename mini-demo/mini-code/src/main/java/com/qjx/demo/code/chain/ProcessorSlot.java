package com.qjx.demo.code.chain;

import com.qjx.demo.code.chain.context.Context;

/**
 * <Description>
 *
 * @author qinjiaxing on 2024/7/22
 * @author <others>
 */
public interface ProcessorSlot<T> {

    void entry(Context context, T param);

    void fireEntry(Context context, Object param);

    void exit(Context context);

    void fireExit(Context context);
}
