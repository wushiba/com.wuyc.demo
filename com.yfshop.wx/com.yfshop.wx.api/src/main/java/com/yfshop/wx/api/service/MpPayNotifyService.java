package com.yfshop.wx.api.service;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.yfshop.wx.api.request.WxPayOrderNotifyReq;

public interface MpPayNotifyService {

    void payOrderNotify(String bizType, WxPayOrderNotifyReq notifyResult);
}
