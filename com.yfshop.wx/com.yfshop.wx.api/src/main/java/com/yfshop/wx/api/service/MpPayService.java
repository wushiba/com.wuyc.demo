package com.yfshop.wx.api.service;

import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.yfshop.wx.api.request.WxEntPayResult;

public interface MpPayService {

    WxPayMpOrderResult createPayOrder(WxPayUnifiedOrderRequest request) throws WxPayException;


    WxPayOrderQueryResult queryOrder(String outTradeNo) throws WxPayException;

    void closeOrder(String outTradeNo);

    WxEntPayResult entPay(EntPayRequest entPayRequest) throws WxPayException;

}
