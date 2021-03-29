package com.yfshop.shop.service.mall.result;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品分类表
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
public class ItemCategoryResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 是否可用(Y|N)
     */
    private String isEnable;

    /**
     * 排序字段
     */
    private Integer sort;
}
