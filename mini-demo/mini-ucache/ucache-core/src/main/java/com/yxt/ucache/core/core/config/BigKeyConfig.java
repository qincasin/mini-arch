package com.yxt.ucache.core.core.config;

import com.yxt.ucache.common.constants.CacheConstants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BigKeyConfig {

    /**
     * 开关
     */
    private boolean enableSizeLimit = CacheConstants.BIGKEY_ENABLESIZELIMIT;
    /**
     * 预警的 缓存大小
     */
    private String warnSize = String.valueOf(CacheConstants.BIGKEY_WARNSIZE);
    /**
     * 预警的 集合类型缓存长度
     */
    private int warnLen = CacheConstants.BIGKEY_WARNLEN;
    /**
     * 拒绝的 缓存大小
     */
    private String forbiddenSize = String.valueOf(CacheConstants.BIGKEY_FORBIDDENSIZE);
    /**
     * 拒绝的 集合类型缓存长度
     */
    private int forbiddenLen = CacheConstants.BIGKEY_FORBIDDENLEN;
    /**
     * 拒绝后是否异常
     */
    private boolean forbiddenException = CacheConstants.FORBIDDEN_EXCEPTION;

}