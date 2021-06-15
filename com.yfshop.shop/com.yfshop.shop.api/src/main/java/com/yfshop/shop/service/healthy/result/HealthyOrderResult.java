package com.yfshop.shop.service.healthy.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author yoush
 * @since 2021-05-26
 */
@Data
public class HealthyOrderResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String orderNo;

    /**
     * 商品id
     */
    private Integer itemId;

    private String itemTitle;

    private String itemSubTitle;

    /**
     * 商品sku售价
     */
    private BigDecimal itemPrice;

    /**
     * 商品封面图
     */
    private String itemCover;

    /**
     * 购买的商品数量
     */
    private Integer itemCount;

    private Integer itemSpec;

    /**
     * 商品描述：口味，尺寸......
     */
    private String itemDesc;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 子订单数量
     */
    private Integer childOrderCount;

    /**
     * 订单总金额
     */
    private BigDecimal orderPrice;

    /**
     * 运费
     */
    private BigDecimal freight;

    /**
     * 实际需要支付的金额
     */
    private BigDecimal payPrice;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 支付流水编号
     */
    private String billNo;

    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime payTime;

    /**
     * 订单取消时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime cancelTime;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    private Integer provinceId;

    private Integer cityId;

    private Integer districtId;

    /**
     * 街道地址
     */
    private String address;

    /**
     * 收货手机号
     */
    private String mobile;

    /**
     * 收货人姓名
     */
    private String contracts;

    /**
     * 配送规则
     */
    private String postRule;
}
