package com.yxt.ucache.core.core;

import com.yxt.ucache.common.CacheBuilder;
import com.yxt.ucache.common.CacheProxy;
import com.yxt.ucache.core.core.config.NamespaceConfig;
import com.yxt.ucache.core.filter.AbsInvokeFilter;
import com.yxt.ucache.core.filter.HeadFilter;
import com.yxt.ucache.core.filter.TailFilter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * 缓存 缓存实例构建者
 *
 * @author qinjiaxing on 2025/2/2
 * @author <others>
 */
public abstract class AbstractCacheBuilder<T extends AbstractCacheBuilder<T>> implements CacheBuilder, Cloneable {

    private final List<AbsInvokeFilter> cacheFilterList = new ArrayList<>();
    protected NamespaceConfig namespaceConfig;
    /**
     * 默第一个filter
     */
    protected AbsInvokeFilter first;
    /**
     * 最后一个filter
     */
    protected AbsInvokeFilter last;

    protected AbstractCacheBuilder(NamespaceConfig namespaceConfig) {
        this.namespaceConfig = namespaceConfig;
    }

    public T addFilters(AbsInvokeFilter... filters) {
        for (AbsInvokeFilter filter : filters) {
            if (StringUtils.isEmpty(filter.getForNamespace()) && namespaceConfig != null) {
                filter.setForNamespace(namespaceConfig.getNamespace());
            }
            cacheFilterList.add(filter);
        }
        return (T) this;
    }

    protected void addHeadFilter(CacheProxy cache) {
        this.first = new HeadFilter(cache);
        if (namespaceConfig != null) {
            first.setForNamespace(namespaceConfig.getNamespace());
        }
    }

    protected void addTailFilter(CacheProxy cache) {
        this.last = new TailFilter(cache);
        if (namespaceConfig != null) {
            last.setForNamespace(namespaceConfig.getNamespace());
        }
    }

    /**
     * 创建缓存实例，并添加缓存实例的filter
     *
     * @param cache
     * @return
     */
    protected CacheProxy createCacheProxy(CacheProxy cache) {
        ((AbsCache) cache).init();
        AbsInvokeFilter firstFilter = null;
        if (first != null) {
            firstFilter = AbsInvokeFilter.buildFilter(cache, first);
        }
        preBuild();

        if (!this.cacheFilterList.isEmpty()) {
            for (AbsInvokeFilter filter : cacheFilterList) {
                UCacheManager.addFilter(filter);
                if (firstFilter == null) {
                    firstFilter = AbsInvokeFilter.buildFilter(cache, filter);
                } else {
                    firstFilter = AbsInvokeFilter.addFilterLast(firstFilter, filter);
                }
            }
        }
        if (last != null) {
            if (firstFilter != null) {
                AbsInvokeFilter.addFilterLast(firstFilter, last);
            } else {
                firstFilter = AbsInvokeFilter.buildFilter(cache, last);
            }
        }
        CacheProxyFactory cacheProxyFactory = new CacheProxyFactory(firstFilter);
        cacheFilterList.clear();
        ;
        return cacheProxyFactory.createCacheProxy(cache);

    }


    protected void preBuild() {

    }

}
