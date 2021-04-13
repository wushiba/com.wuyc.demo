package com.yfshop.wx.api.service;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;

public interface MpService {
    void sendWxMpTemplateMsg(WxMpTemplateMessage wxMpTemplateMessage);
}
