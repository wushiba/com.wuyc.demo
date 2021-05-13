package com.yfshop.wx.api.service;

import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;

import java.util.Date;

public interface MpService {

    void sendWxMpTemplateMsg(WxMpTemplateMessage wxMpTemplateMessage);

    void reSendWxMpTemplateMsg(String openId);

    Void refund(Long orderId, String transactionId) throws WxPayException;;
}
