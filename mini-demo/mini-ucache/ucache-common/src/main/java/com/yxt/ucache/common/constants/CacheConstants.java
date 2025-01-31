package com.yxt.ucache.common.constants;

import com.yxt.ucache.common.enums.CacheType;
import com.yxt.ucache.common.enums.ReportPositionType;

/**
 * CacheConstants
 *
 * @author qinjiaxing on 2025/1/30
 * @author <others>
 */
public interface CacheConstants {

    String DEFAULT = "dft";
    String DEFAULT_NAMESPACE = DEFAULT;
    String DEFAULT_CACHE_NAME = DEFAULT;
    String DEFAULT_KEY = DEFAULT;
    boolean DEFAULT_ENABLED = false;
    int DEFAULT_EXPIRE = 1000 * 60 * 60 * 3;
    int NULL_VALUE_EXPIRE = 1000 * 10;
    int COMPRESS_THRESHOLD = 64 * 1024;
    long WAIT_MILL = 5 * 1000L;
    long RENEWAL_TIME = 1000 * 60L;
    boolean DEFAULT_CACHE_NULL_VALUE = false;

    String UCACHE_CONFIG = "ucacheConfig";
    String UCACHE_BEAN_NAME = "ucache";

    CacheType DEFAULT_CACHE_TYPE = CacheType.REMOTE;
    boolean DEFAULT_LOCAL_ENABLED = false;
    int DEFAULT_LOCAL_EXPIRE_FALLBACK = 1000 * 60 * 3;
    int DEFAULT_LOCAL_EXPIRE = 1000 * 3;
    int DEFAULT_LOCAL_LIMIT = 1000;

    int INVOKE_NORMAL = 0;
    int INVOKE_FALLBACK = 0;

    boolean BIGKEY_ENABLESIZELIMIT = true;
    boolean FORBIDDEN_EXCEPTION = false;
    int BIGKEY_WARNSIZE = 10 * 1024;
    int BIGKEY_WARNLEN = 1000;
    int BIGKEY_FORBIDDENSIZE = 256 * 1024;
    int BIGKEY_FORBIDDENLEN = 10000;

    boolean REPORTER_ENABLE = false;
    String REPORTER_POSITION = ReportPositionType.LOG.getVal();


    int FIVE_MIN = 1000 * 60 * 5;
    int ONE_HOUR = 1000 * 60 * 60;
    int ONE_DAY = 1000 * 60 * 60 * 24;

    int CODE_OK = 0;
    int CODE_ERROR_NET = 10;
    int CODE_ERROR_CODEC = 20;
    int CODE_ERROR_CAST = 25;
    int CODE_ERROR_FILTER = 30;
    int CODE_ERROR_PARAM = 80;
    int CODE_ERROR_OTHER = 99;

    static String cacheBeanName(String ns) {
        return UCACHE_BEAN_NAME + "_" + ns;
    }
}
