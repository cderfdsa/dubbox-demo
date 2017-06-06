package com.cyf.base.common.exception;

/**
 * Created by Administrator on 2017/6/4.
 */
public class BizException extends RuntimeException {
    private String errorCode;
    private String msg;

    public BizException(String msg){
        this(null, msg);
    }

    public BizException(String errorCode, String msg){
        super();
        this.errorCode = errorCode;
        this.msg = msg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
