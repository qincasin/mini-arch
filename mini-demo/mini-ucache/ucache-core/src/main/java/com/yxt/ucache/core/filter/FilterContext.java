package com.yxt.ucache.core.filter;

import com.google.common.base.Objects;
import com.yxt.ucache.common.CacheProxy;
import com.yxt.ucache.common.ValWrapper;
import com.yxt.ucache.common.constants.CacheConstants;
import com.yxt.ucache.common.enums.OpType;
import com.yxt.ucache.core.core.config.NamespaceConfig;
import com.yxt.ucache.core.utils.InnerObjectUtils;
import java.lang.reflect.Method;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * 缓存filter的上下文，每次缓存操作但如果需要经过filter，则需要先生成该上下文，表示本次缓存操作各种信息
 *
 * @author qinjiaxing on 2025/1/31
 * @author <others>
 */
@Getter
@Setter
public class FilterContext {

    /**
     * 缓存key
     */
    private final String key;
    /**
     * 缓存key集合
     */
    private final Set<String> keys;
    /**
     * 缓存操作类型
     */
    private final OpType opType;
    /**
     * 缓存操作的实例对象
     */
    private CacheProxy cache;
    /**
     * 缓存实例对象所属的配置
     */
    private NamespaceConfig namespaceConfig;
    /**
     * 缓存操作方法
     */
    private Method method;
    /**
     * 缓存操作方法参数
     */
    private Object[] args;
    /**
     * 缓存值结果或者入参(具体要看缓存操作类型)
     */
    private ValWrapper valWrapper;
    /**
     * 非key-val类型的缓存的此次操作val
     */
    private Object val;
    /**
     * 缓存操作结果code
     */
    private int resCode = CacheConstants.CODE_OK;
    /**
     * 缓存操作时间, 单位毫秒
     */
    private long opMillis;
    /**
     * 缓存是否命中
     */
    private boolean hit;


    public FilterContext(CacheProxy cache, NamespaceConfig namespaceConfig, String key, Set<String> keys,
                         OpType opType, Method method, Object[] args) {
        this.cache = cache;
        this.key = key;
        this.keys = keys;
        this.namespaceConfig = namespaceConfig;
        this.opType = opType;
        this.method = method;
        this.args = args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilterContext)) {
            return false;
        }
        FilterContext that = (FilterContext) o;
        return InnerObjectUtils.eq(key, that.key) && InnerObjectUtils.eq(keys, that.keys) && opType == that.opType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key, keys, opType);
    }


}
