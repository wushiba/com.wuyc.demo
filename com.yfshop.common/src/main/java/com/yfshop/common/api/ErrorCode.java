package com.yfshop.common.api;

/**
 * 枚举了一些常用API操作码
 * Created by mu on 2019/4/19.
 */
public class ErrorCode implements IErrorCode {

    private int code;
    private String message;

    public ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
