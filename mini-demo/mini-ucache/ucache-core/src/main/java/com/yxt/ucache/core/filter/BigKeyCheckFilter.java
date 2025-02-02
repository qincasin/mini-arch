package com.yxt.ucache.core.filter;

import com.yxt.ucache.core.core.config.BigKeyConfig;

/**
 * 用于检测和管理 bigkey 的 filter
 * 可通过配置对bigkey进行预警和终止缓存操作
 * <p>
 * 注意: 该filter仅支持远程缓存, 例如redis; 同时该filter需要在缓存值序列化之后才能得到缓存大小, 所以需要在 CodecFilter 之后,
 * 否则该filter 将不生效
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public class BigKeyCheckFilter extends AbsInvokeFilter {

    private final BigKeyConfig bigKeyConfig;

    public BigKeyCheckFilter(BigKeyConfig bigKeyConfig, String namespace) {
        super("BigKeyCheck", namespace, null);
        this.bigKeyConfig = bigKeyConfig;
    }

    @Override
    public Object invoke(FilterContext context) {
        return super.invoke(context);
    }

    @Override
    protected boolean canProcess(FilterContext context) {
        return false;
    }
}
