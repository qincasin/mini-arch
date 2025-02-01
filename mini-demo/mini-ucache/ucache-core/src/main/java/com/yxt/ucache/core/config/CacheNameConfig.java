package com.yxt.ucache.core.config;

import com.yxt.ucache.common.constants.CacheConstants;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * CacheNameConfig
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
@Getter
@Setter
public class CacheNameConfig extends InheritableConfig<NamespaceConfig> {

    private String cacheName = CacheConstants.DEFAULT_CACHE_NAME;
    private Map<String, KeyConfig> key = new HashMap<>();
    private Annotation annotation;


}
