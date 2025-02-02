package com.yxt.ucache.core.core;

import com.yxt.ucache.core.core.config.InternalConfig;
import com.yxt.ucache.core.core.config.NamespaceConfig;
import com.yxt.ucache.core.utils.InnerAssertUtils;
import java.time.Duration;

/**
 * <Description>
 *
 * @author qinjiaxing on 2025/2/2
 * @author <others>
 */
public abstract class InternalCacheBuilder<T extends InternalCacheBuilder<T>> extends AbstractCacheBuilder<T> {

    protected InternalConfig internalConfig;
    protected int maximumSize;
    protected Duration expireAfterWrite;

    public InternalCacheBuilder(NamespaceConfig namespaceConfig, InternalConfig internalConfig) {
        super(namespaceConfig);
        this.internalConfig = internalConfig;
    }

    public T buildMaxSize(int maximumSize) {
        InnerAssertUtils.isTrue(maximumSize > 0, "'maximumSize' can't be negative or zero");
        this.maximumSize = maximumSize;
        return (T) this;
    }

    public T buildExpire(Duration expireAfterWrite) {
        InnerAssertUtils.isTrue(expireAfterWrite == null || expireAfterWrite.toMillis() > 0, "'expireAfterWrite' can't be negative or zero");
        this.expireAfterWrite = expireAfterWrite;
        return (T) this;
    }

    @Override
    protected void preBuild() {
    }
}
