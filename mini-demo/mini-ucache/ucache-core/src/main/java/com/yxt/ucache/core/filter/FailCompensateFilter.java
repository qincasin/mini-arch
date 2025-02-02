package com.yxt.ucache.core.filter;

import com.yxt.ucache.common.enums.CacheType;
import com.yxt.ucache.common.enums.OpType;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * <Description>
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public class FailCompensateFilter extends RemoveCompensateFilter {

    public FailCompensateFilter(String namespace) {
        super(2000, 3, 3, "FailCompensateFilter", namespace);
    }

    @Override
    public Object invoke(FilterContext context) {
        boolean canProcess = canProcess(context);
        if (!canProcess) {
            return super.invoke(context);
        }
        Object key = StringUtils.isNotEmpty(context.getKey()) ? context.getKey() : context.getKeys();
        RemovedKey removedKey = new RemovedKey(key);
        try {
            Object invoke = super.invoke(context);
            keyQueue.remove(removedKey);
            return invoke;
        } catch (Exception e) {
            try {
                addTask(removedKey);
            } catch (Exception exception) {
                logger.warn("UCache 失败补偿调用报错 key={}", removedKey, exception);
            }
            throw e;
        }
    }

    @Override
    protected boolean canProcess(FilterContext context) {
        // 只有删除和新增才需要重试, 重试的方式都是删除, 因为新增失败可能导致的是更新失败, 所以重试使用删除
        if (Objects.equals(context.getCache().getCacheType(), CacheType.REMOTE)) {
            return OpType.PUT.equals(context.getOpType()) || OpType.REMOVE.equals(context.getOpType());
        }
        return false;
    }
}
