package com.yfshop.admin.service;

import com.yfshop.admin.api.wx.MpService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@DubboService
@EnableAsync
public class MpServiceImpl implements MpService {

   @Autowired
    private WxMpTemplateMsgService wxMpTemplateMsgService;
    @Async
    @Override
    public void sendWxMpTemplateMsg(WxMpTemplateMessage wxMpTemplateMessage) throws WxErrorException {
        wxMpTemplateMsgService.sendTemplateMsg(wxMpTemplateMessage);
    }
}
