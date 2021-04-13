package com.yfshop.wx.api.service;

import com.yfshop.wx.api.request.WxPayOrderNotifyReq;

public interface MpPayNotifyService {

    void payOrderNotify(String bizType, WxPayOrderNotifyReq notifyResult);
}
