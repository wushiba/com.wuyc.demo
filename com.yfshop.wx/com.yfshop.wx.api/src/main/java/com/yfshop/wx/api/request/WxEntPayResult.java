package com.yfshop.wx.api.request;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class WxEntPayResult implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 商户订单号.
     */
    private String partnerTradeNo;

    /**
     * 微信订单号.
     */
    private String paymentNo;

    /**
     * 微信支付成功时间.
     */
    private String paymentTime;



}
