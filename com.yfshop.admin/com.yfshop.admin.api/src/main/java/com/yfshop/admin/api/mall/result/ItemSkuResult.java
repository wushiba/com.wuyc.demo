package com.yfshop.admin.api.mall.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 平台商品sku表
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
public class ItemSkuResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 商品id
     */
    private Integer itemId;

    /**
     * 一级分类id
     */
    private Integer categoryId;

    /**
     * 商品标题
     */
    private String skuTitle;

    /**
     * 副标题(子标题)
     */
    private String skuSubTitle;

    /**
     * 商品售价(用于下单结算的价格)
     */
    private BigDecimal skuSalePrice;

    /**
     * 市场价
     */
    private BigDecimal skuMarketPrice;

    /**
     * 库存数量
     */
    private Integer skuStock;

    /**
     * 封面图
     */
    private String skuCover;

    /**
     * 规格id字符串(1,2,3,4,5)
     */
    private String specValueIdPath;

    /**
     * 规格名称和值的json串({"尺码":"38","颜色":"黑色"})
     */
    private String specNameValueJson;

    /**
     * 是否启用该商品(Y|N)
     */
    private String isEnable;

    /**
     * 排序字段
     */
    private Integer sort;
}
