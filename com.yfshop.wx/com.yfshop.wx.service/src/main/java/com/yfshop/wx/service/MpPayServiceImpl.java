package com.yfshop.wx.service;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.yfshop.wx.api.service.MpPayService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class MpPayServiceImpl implements MpPayService {
    @Autowired
    private WxPayService wxPayService;

    @Override
    public WxPayMpOrderResult createPayOrder(WxPayUnifiedOrderRequest request) throws WxPayException {
        request.setSignType(WxPayConstants.SignType.HMAC_SHA256);
        WxPayMpOrderResult wxPayMpOrderResult=wxPayService.createOrder(request);
        System.out.println(wxPayMpOrderResult.toString());
        return wxPayMpOrderResult;
    }


}
