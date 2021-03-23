package com.yfshop.admin.api.mall.result;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品图片表
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
public class ItemImageResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

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
}
