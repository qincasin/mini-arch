package com.yxt.ucache.core.core;

import com.yxt.ucache.common.CacheProxy;
import com.yxt.ucache.core.filter.AbsInvokeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于组装Cache调用关系, 并生成代理对象
 *
 * @author qinjiaxing on 2025/2/2
 * @author <others>
 */
public class CacheProxyFactory {

    private static Logger logger = LoggerFactory.getLogger(CacheProxyFactory.class);
    private AbsInvokeFilter filter;

    public CacheProxyFactory(AbsInvokeFilter filter) {
        this.filter = filter;
    }


    /**
     * 创建缓存代理对象
     *
     * @param cache
     * @return
     */
    public CacheProxy createCacheProxy(CacheProxy cache) {
        return null;
    }
}
