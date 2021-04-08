package com.yfshop.wx.api.service;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;

public interface MpPayService {

    WxPayMpOrderResult createPayOrder(WxPayUnifiedOrderRequest request) throws WxPayException;

}
