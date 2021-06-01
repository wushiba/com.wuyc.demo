package com.yfshop.wx.api.service;

import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.yfshop.wx.api.request.WxPayRefundReq;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;

import java.util.Date;

public interface MpService {

    void sendWxMpTemplateMsg(WxMpTemplateMessage wxMpTemplateMessage);

    void reSendWxMpTemplateMsg(String openId);

    Void refund(WxPayRefundReq wxPayRefundReq) throws WxPayException;;
}
