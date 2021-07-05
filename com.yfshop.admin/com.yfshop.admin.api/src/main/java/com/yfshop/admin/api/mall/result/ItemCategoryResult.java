package com.yfshop.admin.api.mall.result;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime updateTime;

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

    private Long itemCount;
}
