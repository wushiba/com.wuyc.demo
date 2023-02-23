package com.wuyc.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sp0313
 * @date 2022年11月28日 09:56:00
 */
@Data
public class Result<T> implements Serializable {


    public static final int SUCCESS_STATUS = 200;
    public static final int SUCCESS_FAIL = 400;
    public static final String SUCCESS_MESSAGE = "操作成功";
    public static final String FAIL_CODE = "maskit.failure.general";
    public static final String SUCCESS_CODE = "maskit.success.general";


    public T data;

    public String code;

    public String message;

    public Integer status;

    @JsonIgnore
    private Object[] args = null;

    public Result() {

    }

    public Result(int status, String code, String message, T data) {
        this.code = code;
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public static <T> Result<T> success(T data) {
        return Result.success(SUCCESS_STATUS, SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    public static <T> Result<T> success(Integer status, String code, T data) {
        return new Result(status, code, SUCCESS_MESSAGE, data);
    }

    public static <T> Result<T> success(Integer status, String code, String message, T data) {
        return new Result(status, code, message, data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result(SUCCESS_FAIL, FAIL_CODE, message, null);
    }

    public void args(Object... args) {
        this.args = args;
    }

    public Object[] args() {
        return this.args;
    }
}
