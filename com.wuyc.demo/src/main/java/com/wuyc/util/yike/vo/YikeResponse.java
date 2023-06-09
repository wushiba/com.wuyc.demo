package com.wuyc.util.yike.vo;

import lombok.Data;

import java.util.Objects;

/**
 * @author sp0313
 * @date 2023年06月02日 09:54:00
 */
@Data
public class YikeResponse<T> {

    private T data;

    /**
     * false代表成功 、true代表失败
     */
    private Boolean error;


    /**
     * 操作成功
     *
     * @param response 亿客返回的数据
     * @return true -> 操作成功、false -> 操作失败
     */
    public static boolean success(YikeResponse response) {
        return Objects.nonNull(response)
                && Objects.nonNull(response.error)
                && !response.error;
    }

    /**
     * 是否操作失败
     *
     * @param response 亿客返回的数据
     * @return true -> 操作失败、false -> 操作成功
     */
    public static boolean fail(YikeResponse response) {
        return Objects.isNull(response)
                || Objects.isNull(response.error)
                || response.error;
    }


}
