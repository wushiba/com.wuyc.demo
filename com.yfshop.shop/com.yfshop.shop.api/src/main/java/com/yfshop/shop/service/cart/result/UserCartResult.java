package com.yfshop.shop.service.cart.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户购物车
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
public class UserCartResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 商户id
     */
    private Integer itemId;

    private Integer skuId;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 商品标题
     */
    private String skuTitle;

    /**
     * 副标题(子标题)
     */
    private String skuSubTitle;

    /**
     * 封面图
     */
    private String skuCover;

    /**
     * 规格值id字符串(1,2,3,4,5)
     */
    private String specValueIdPath;

    /**
     * 规格名称和值的json串({"尺码":"38","颜色":"黑色"})
     */
    private String specNameValueJson;

    /**
     * 商品售价
     */
    private BigDecimal skuSalePrice;


    private BigDecimal payPrice;

    private BigDecimal orderPrice;

    private BigDecimal couponPrice;

    private Long userCouponId;
    /**
     * 市场价(用于展示)
     */
    private BigDecimal skuMarketPrice;

    private BigDecimal freight;

    /**
     * 商品是否有效的(Y|N)
     */
    private String isAvailable;

    /**
     * 一级分类id
     */
    private Integer categoryId;

    private String skuType;
}
