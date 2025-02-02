package com.yxt.ucache.core.filter;

import com.yxt.ucache.common.CacheProxy;
import com.yxt.ucache.core.utils.CacheContextUtils;

/**
 * 虚拟头结点filter, 是距离用户操作缓存组件最近的filter
 * 目前只有一个作用就是将上下文放到 ThreadLocal 中, 以便后面其他组件使用
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public class HeadFilter extends AbsInvokeFilter {

    public HeadFilter(CacheProxy target) {
        super("HeadFilter", null, target);
    }

    @Override
    public Object invoke(FilterContext context) {
        boolean isHead = CacheContextUtils.getFilterContext() == null;
        if (isHead) {
            CacheContextUtils.addFilterContext(context);
        }
        try {
            return super.invoke(context);
        } finally {
            if (isHead) {
                CacheContextUtils.removeFilterContext();
            }
        }
    }

    @Override
    protected boolean canProcess(FilterContext context) {
        return false;
    }
}
