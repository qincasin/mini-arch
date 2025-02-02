package com.yxt.ucache.core.filter;

import com.yxt.ucache.common.CacheProxy;
import com.yxt.ucache.common.ValWrapper;
import com.yxt.ucache.common.constants.CacheConstants;
import com.yxt.ucache.common.enums.OpType;
import com.yxt.ucache.core.exception.CacheInterruptException;
import java.util.Objects;

/**
 * 虚拟尾部过滤器, 是距离缓存组件最近的filter
 * 所以功能有
 * 1. 记录缓存组件操作时间,
 * 2. 包装原始缓存组件异常, 并决定是否抛出该异常
 * 3. 保存缓存组件的操作结果包括状态
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public class TailFilter extends AbsInvokeFilter {

    public TailFilter(CacheProxy target) {
        super("TailFilter", null, target);
    }

    @Override
    public Object invoke(FilterContext context) {
        Object result = null;
        long start = System.currentTimeMillis();
        try {
            result = super.invoke(context);
        } catch (Exception ex) {
            logger.warn("UCache 实例调用异常 {}, ex", getTarget().getClass().getName(), ex);
            if (ex instanceof CacheInterruptException) {
                context.setResCode(((CacheInterruptException) ex).getCode());
                if (context.getResCode() == CacheConstants.CODE_ERROR_NET) {
                    logger.warn("UCache 实例调用异常: 组件异常, 向上抛出 {}", getTarget().getClass().getName());
                    throw ex;
                }
            } else {
                context.setResCode(CacheConstants.CODE_ERROR_OTHER);
            }
        } finally {
            context.setOpMillis(System.currentTimeMillis() - start);
        }
        if (Objects.equals(context.getOpType(), OpType.GET) && result != null) {
            if (context.getMethod().getReturnType() == ValWrapper.class) {
                context.setValWrapper((ValWrapper) result);
                context.setHit(true);
            }
        }
        return result;
    }

    @Override
    protected boolean canProcess(FilterContext context) {
        return false;
    }
}
