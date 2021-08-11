package com.yfshop.wx.service;

import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayOrderQueryRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.wx.api.service.MpPayService;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

@DubboService
public class MpPayServiceImpl implements MpPayService {
    @Autowired
    private WxPayService wxPayService;

    @Override
    public WxPayMpOrderResult createPayOrder(WxPayUnifiedOrderRequest request) throws WxPayException {
        WxPayMpOrderResult wxPayMpOrderResult = wxPayService.createOrder(request);
        return wxPayMpOrderResult;
    }

    @Override
    public WxPayOrderQueryResult queryOrder(String outTradeNo) throws WxPayException {
        WxPayOrderQueryRequest wxPayOrderQueryRequest = new WxPayOrderQueryRequest();
        wxPayOrderQueryRequest.setOutTradeNo(outTradeNo);
        return wxPayService.queryOrder(wxPayOrderQueryRequest);
    }

    @SneakyThrows
    @Async
    @Override
    public void closeOrder(String outTradeNo) {
        wxPayService.closeOrder(outTradeNo);
    }

    @Override
    public EntPayResult entPay(EntPayRequest entPayRequest) throws WxPayException {
       return wxPayService.getEntPayService().entPay(entPayRequest);
    }
}
