package com.yfshop.admin.api.wx;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;

public interface MpPayService {

    WxPayMpOrderResult createPayOrder(WxPayUnifiedOrderRequest request) throws WxPayException;

}
