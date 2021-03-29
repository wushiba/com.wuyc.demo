package com.yfshop.shop.service.mall.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品规则值表
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
public class ItemSpecValueResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 商品编号
     */
    private Integer itemId;

    /**
     * 规格名称id
     */
    private Integer specId;

    /**
     * 规格的值
     */
    private String specValue;

    /**
     * 排序字段
     */
    private Integer sort;
}
