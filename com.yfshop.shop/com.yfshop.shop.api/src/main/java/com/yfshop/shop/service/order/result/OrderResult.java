package com.yfshop.shop.service.order.result;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 用户id
     */
    private Integer userId;

    private Integer userCouponId;

    /**
     * 商品总数量
     */
    private Integer itemCount;

    /**
     * 子订单数量
     */
    private Integer childOrderCount;

    /**
     * 订单总金额
     */
    private BigDecimal orderPrice;

    /**
     * 优惠券面额
     */
    private BigDecimal couponPrice;

    /**
     * 运费
     */
    private BigDecimal freight;

    /**
     * 实际需要支付的金额
     */
    private BigDecimal payPrice;

    private String isPay;

    /**
     * 支付流水编号
     */
    private String billNo;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 订单取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 支付重试次数
     */
    private Integer payEntryCount;

    /**
     * 订单备注
     */
    private String remark;

    private List<OrderDetailResult> childList;

    private OrderAddressResult orderAddressResult;

}
