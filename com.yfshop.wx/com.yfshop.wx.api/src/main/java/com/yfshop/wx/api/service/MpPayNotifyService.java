package com.yfshop.wx.api.service;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;

public interface MpPayNotifyService {

    void payOrderNotify(String bizType, WxPayOrderNotifyResult notifyResult);
}
