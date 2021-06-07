package com.yfshop.admin.api.healthy.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 商品图片表
 * </p>
 *
 * @author yoush
 * @since 2021-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HealthyActResult implements Serializable {

    private Integer id;

    /**
     * 商品编号
     */
    private String actName;

    /**
     * 商品编号
     */
    private Integer itemId;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 排序字段
     */
    private Integer sort;

    private String isEnable;

    private String content;

}
