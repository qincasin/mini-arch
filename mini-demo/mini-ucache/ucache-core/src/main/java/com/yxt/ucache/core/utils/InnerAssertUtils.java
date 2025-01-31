package com.yxt.ucache.core.utils;


import com.yxt.ucache.common.constants.CacheConstants;
import com.yxt.ucache.core.exception.CacheInterruptException;

public class InnerAssertUtils {

    private InnerAssertUtils() {

    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_PARAM, message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_PARAM, message);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_PARAM, message);
        }
    }
}
