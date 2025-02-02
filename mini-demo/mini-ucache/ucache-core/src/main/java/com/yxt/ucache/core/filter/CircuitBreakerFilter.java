package com.yxt.ucache.core.filter;

import com.yxt.ucache.common.CacheProxy;
import com.yxt.ucache.core.core.config.UCacheConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;

/**
 * <Description>
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public class CircuitBreakerFilter extends AbsInvokeFilter {

    private UCacheConfig cacheConfig;
    private CircuitBreaker circuitBreaker;
    private CacheProxy fallbackCache;

    public CircuitBreakerFilter(UCacheConfig cacheConfig, String namespace) {
        super("CircuitBreakerFilter", namespace, null);
        this.cacheConfig = cacheConfig;
        initFallbackCache();
        initCircuitBreaker();
    }

    @Override
    public Object invoke(FilterContext context) {
        return super.invoke(context);
    }

    private void initCircuitBreaker() {

    }

    private void initFallbackCache() {

    }

    @Override
    protected boolean canProcess(FilterContext context) {
        return false;
    }
}
