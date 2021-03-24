package com.yfshop.common.api;

import java.io.Serializable;

/**
 * 封装API的错误码
 */
public interface IErrorCode extends Serializable {
    int getCode();

    String getMessage();
}
