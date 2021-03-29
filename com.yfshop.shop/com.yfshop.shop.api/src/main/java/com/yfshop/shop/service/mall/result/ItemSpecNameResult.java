package com.yfshop.shop.service.mall.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 商品规则名表
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
public class ItemSpecNameResult implements Serializable {
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
     * 规格名称
     */
    private String specName;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 商品的规格值
     */
    private List<ItemSpecValueResult> specValues;
}
