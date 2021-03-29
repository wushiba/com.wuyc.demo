package com.yfshop.shop.service.mall.result;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
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
