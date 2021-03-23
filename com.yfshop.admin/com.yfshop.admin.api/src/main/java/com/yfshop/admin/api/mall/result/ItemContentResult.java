package com.yfshop.admin.api.mall.result;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品详情表
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
public class ItemContentResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 商品编号
     */
    private Integer itemId;

    /**
     * 商品详情,富文本内容
     */
    private String content;
}
