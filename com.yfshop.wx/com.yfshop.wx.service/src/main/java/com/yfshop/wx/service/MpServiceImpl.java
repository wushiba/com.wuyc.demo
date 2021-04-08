package com.yfshop.wx.service;


import com.yfshop.wx.api.service.MpService;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@DubboService
@EnableAsync
@AllArgsConstructor
public class MpServiceImpl implements MpService {

    private final WxMpService wxMpService;

    @Async
    @Override
    public void sendWxMpTemplateMsg(WxMpTemplateMessage wxMpTemplateMessage) throws WxErrorException {
        wxMpService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
    }
}
