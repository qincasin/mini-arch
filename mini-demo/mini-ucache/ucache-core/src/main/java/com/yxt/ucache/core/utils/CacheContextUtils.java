package com.yxt.ucache.core.utils;

import com.yxt.ucache.core.filter.FilterContext;
import com.yxt.ucache.core.filter.LoadValContext;

/**
 * CacheContextUtils
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public final class CacheContextUtils {

    private static ThreadLocal<FilterContext> cacheFilter = new InheritableThreadLocal<>();
    private static ThreadLocal<LoadValContext> loadVal = new InheritableThreadLocal<>();

    private CacheContextUtils() {
    }

    public static void addFilterContext(FilterContext context) {
        cacheFilter.set(context);
    }

    public static FilterContext getFilterContext() {
        return cacheFilter.get();
    }

    public static void removeFilterContext() {
        cacheFilter.remove();
    }

    public static void addLoadValContext(LoadValContext context) {
        loadVal.set(context);
    }

    public static LoadValContext getLoadValContext() {
        return loadVal.get();
    }

    public static void removeLoadValContext() {
        loadVal.remove();
    }

}
