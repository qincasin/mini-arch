package com.yxt.ucache.common.exception;

/**
 * @author lzw
 * @version :
 * @date 2022/10/21 16:47:32
 */
public class UCacheException extends RuntimeException {

    private final String errorMsg;

    public UCacheException(final String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }


    public String getErrorMsg() {
        return errorMsg;
    }


}
