package com.yfshop.admin.api.order.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResult implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime updateTime;

    private Long id;

    private String orderNo;
    /**
     * 用户id
     */
    private Integer userName;

    /**
     * 订单总金额
     */
    private BigDecimal orderPrice;

    /**
     * 优惠券面额
     */
    private BigDecimal couponPrice;

    /**
     * 实际需要支付的金额
     */
    private BigDecimal payPrice;

    /**
     * 配送方式
     */
    private String receiveWay;

    private String orderStatus;
}
