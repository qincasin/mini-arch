package com.yxt.ucache.core.filter;

import com.google.common.base.Objects;
import com.yxt.ucache.common.CacheProxy;
import com.yxt.ucache.common.Filter;
import com.yxt.ucache.core.core.CacheProxyInvoker;
import com.yxt.ucache.core.utils.InnerObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存流程处理器
 * 原则上 该处理器 尽可能不要影响缓存流程, 除非抛出 CacheInterruptException 异常
 * 影响流程的影响包括, 中断流程, 阻塞流程, 慢响应流程等
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public abstract class AbsInvokeFilter implements Filter<FilterContext> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String name;
    private String forNamespace;
    private CacheProxy target;

    private AbsInvokeFilter nextFilter;
    private CacheProxyInvoker cacheProxyInvoker;

    public AbsInvokeFilter(String name, String forNamespace, CacheProxy target) {
        this.name = name;
        this.forNamespace = forNamespace;
        this.target = target;
        if (target != null) {
            this.cacheProxyInvoker = CacheProxyInvoker.reflectInvoker;
        }
    }

    public static AbsInvokeFilter addFilterLast(AbsInvokeFilter firstFilter, AbsInvokeFilter newFilter) {
        if (newFilter == null) {
            return firstFilter;
        }
        getLast(firstFilter).setNext(newFilter);
        newFilter.setTarget(firstFilter.getTarget());
        return firstFilter;
    }

    /**
     * 在filter后面添加
     *
     * @param firstFilter
     * @param newFilter
     * @return
     */
    public static AbsInvokeFilter addFilterNext(AbsInvokeFilter firstFilter, AbsInvokeFilter newFilter) {
        if (newFilter == null) {
            return firstFilter;
        }
        firstFilter.setNext(newFilter);
        newFilter.setTarget(firstFilter.getTarget());
        return firstFilter;
    }

    /**
     * 在filter前面添加
     *
     * @param firstFilter
     * @param filterName
     * @param newFilter
     * @return
     */
    public static AbsInvokeFilter addFilterBefore(AbsInvokeFilter firstFilter, String filterName, AbsInvokeFilter newFilter) {
        if (newFilter == null) {
            return firstFilter;
        }
        AbsInvokeFilter current = firstFilter, prev = null;
        while (!current.getName().equals(filterName)) {
            prev = current;
            current = current.getNext();
        }
        if (current.getName().equals(filterName)) {
            if (prev == null) {
                addFilterLast(newFilter, firstFilter);
                return newFilter;
            }
            addFilterNext(prev, newFilter);
            addFilterLast(newFilter, current);
        }
        return firstFilter;
    }

    /**
     * 在filter后面添加
     *
     * @param firstFilter
     * @param filterName
     * @param newFilter
     * @return
     */
    public static AbsInvokeFilter addFilterAfter(AbsInvokeFilter firstFilter, String filterName, AbsInvokeFilter newFilter) {
        if (newFilter == null) {
            return firstFilter;
        }
        AbsInvokeFilter current = firstFilter;
        while (current != null && !current.getName().equals(filterName)) {
            current = current.getNext();
        }
        if (current != null && current.getName().equals(filterName)) {
            AbsInvokeFilter next = current.getNext();
            addFilterNext(current, newFilter);
            if (next != null) {
                addFilterLast(current, next);
            }
        }
        return firstFilter;
    }

    public static AbsInvokeFilter buildFilter(CacheProxy target, AbsInvokeFilter... newFilters) {
        if (target == null || newFilters == null || newFilters.length == 0) {
            throw new IllegalArgumentException("参数不能为空");
        }

        AbsInvokeFilter next = null;
        for (int i = newFilters.length - 1; i >= 0; i--) {
            newFilters[i].setNext(next);
            newFilters[i].setTarget(target);
            next = newFilters[i];
        }
        return newFilters[0];
    }

    /**
     * 获取最后一个
     *
     * @param firstFilter
     * @return
     */
    public static AbsInvokeFilter getLast(AbsInvokeFilter firstFilter) {
        AbsInvokeFilter last = firstFilter;
        while (last.getNext() != null) {
            last = last.getNext();
        }
        return last;
    }

    @Override
    public Object invoke(FilterContext context) {
        if (nextFilter != null) {
            return nextFilter.invoke(context);
        }
        return cacheProxyInvoker.invoke(target, context.getMethod(), context.getArgs());
    }

    /**
     * can process
     *
     * @param context
     * @return
     */
    protected abstract boolean canProcess(FilterContext context);

    public AbsInvokeFilter getNext() {
        return nextFilter;
    }

    public AbsInvokeFilter setNext(AbsInvokeFilter nextFilter) {
        this.nextFilter = nextFilter;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getForNamespace() {
        return forNamespace;
    }

    public CacheProxy getTarget() {
        return target;
    }

    public void setTarget(CacheProxy target) {
        this.target = target;
        this.cacheProxyInvoker = CacheProxyInvoker.reflectInvoker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbsInvokeFilter)) {
            return false;
        }
        AbsInvokeFilter that = (AbsInvokeFilter) o;
        return InnerObjectUtils.eq(name, that.name) && InnerObjectUtils.eq(forNamespace, that.forNamespace);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, forNamespace);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(name).append("] => ");
        AbsInvokeFilter f = this.nextFilter;
        while (f != null) {
            sb.append("[").append(f.name).append("] => ");
            if (f == this) {
                sb.append("[循环]");
                break;
            }
            f = f.nextFilter;
        }
        return sb.toString();
    }

}
