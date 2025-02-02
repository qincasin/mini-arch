package com.yxt.ucache.core.core;

import com.yxt.ucache.core.core.config.NamespaceConfig;
import com.yxt.ucache.core.filter.BigKeyCheckFilter;
import com.yxt.ucache.core.filter.CircuitBreakerFilter;
import com.yxt.ucache.core.filter.CodecFilter;
import com.yxt.ucache.core.filter.FailCompensateFilter;

/**
 * ExternalCacheBuilder 抽象的 外部缓存的构造器 builder
 *
 * @author qinjiaxing on 2025/2/2
 * @author <others>
 */
public abstract class ExternalCacheBuilder<T extends ExternalCacheBuilder> extends AbstractCacheBuilder<ExternalCacheBuilder<T>> {

    public ExternalCacheBuilder(NamespaceConfig namespaceConfig) {
        super(namespaceConfig);
    }

    @Override
    protected void preBuild() {
        super.preBuild();

        String namespace = namespaceConfig.getNamespace();

        // 添加拦截器
        // 编解码
        addFilters(new CodecFilter(namespaceConfig));
        // 大key拦截器
        addFilters(new BigKeyCheckFilter(namespaceConfig.getParent().getBigKey(), namespace));
        // 熔断器
        addFilters(new CircuitBreakerFilter(namespaceConfig.getParent(), namespace));
        // 删除失败补偿器
        addFilters(new FailCompensateFilter(namespace));
    }
}
