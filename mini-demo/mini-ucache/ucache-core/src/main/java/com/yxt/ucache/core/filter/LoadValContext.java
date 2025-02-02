package com.yxt.ucache.core.filter;

import com.yxt.ucache.common.ValWrapper;
import lombok.Getter;
import lombok.Setter;

/**
 * LoadValContext
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
@Getter
@Setter
public class LoadValContext {

    private String namespace;
    private String cacheName;
    private String fullKey;

    private ValWrapper valWrapper;
    private long start;
    private long opMillis;
    private Throwable throwable;

    public LoadValContext(String namespace, String cacheName, String fullKey) {
        this.namespace = namespace;
        this.cacheName = cacheName;
        this.fullKey = fullKey;
    }

}
