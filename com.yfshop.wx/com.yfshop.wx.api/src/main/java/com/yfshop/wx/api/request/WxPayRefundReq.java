package com.yfshop.wx.api.request;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class WxPayRefundReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDateTime createTime;

    private String openId;

    /**
     * 订单总金额，单位为分
     */
    private Integer totalFee;


    private Integer refundFee;
    /**
     * 微信支付订单号
     */
    private String transactionId;

    /**
     * 商户订单号
     */
    private String outtradeNo;

    /**
     * 退款订单
     */
    private String refundNo;



}
