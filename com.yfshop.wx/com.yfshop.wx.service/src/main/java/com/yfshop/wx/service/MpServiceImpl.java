package com.yfshop.wx.service;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.reflect.TypeToken;
import com.yfshop.code.mapper.WxTemplateMessageMapper;
import com.yfshop.code.model.WxTemplateMessage;
import com.yfshop.common.util.DateUtil;
import com.yfshop.wx.api.service.MpService;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@DubboService
public class MpServiceImpl implements MpService {
    @Autowired
    private WxMpService wxMpService;
    @Resource
    private WxTemplateMessageMapper wxTemplateMessageMapper;

    @Async
    @Override
    public void sendWxMpTemplateMsg(WxMpTemplateMessage wxMpTemplateMessage) {
        try {
            wxMpService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
            saveWxTemplateMessage(wxMpTemplateMessage, "SUCCESS");
        } catch (WxErrorException e) {
            saveWxTemplateMessage(wxMpTemplateMessage, "FAIL");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    @Override
    public void reSendWxMpTemplateMsg(String openId) {
        WxTemplateMessage wxTemplateMessage = wxTemplateMessageMapper.selectOne(Wrappers.<WxTemplateMessage>lambdaQuery()
                .eq(WxTemplateMessage::getOpenId, openId)
                .ge(WxTemplateMessage::getCreateTime, DateUtil.getDate(new Date()))
                .orderByDesc(WxTemplateMessage::getId));
        if (wxTemplateMessage != null&&"FAIL".equals(wxTemplateMessage.getStatus())) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String dataJson = wxTemplateMessage.getData();
            List<WxMpTemplateData> data = WxMpGsonBuilder.create().fromJson(dataJson, new TypeToken<List<WxMpTemplateData>>() {
            }.getType());
            WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder()
                    .data(data)
                    .toUser(openId)
                    .url(wxTemplateMessage.getUrl())
                    .templateId(wxTemplateMessage.getTemplateId())
                    .build();
            try {
                wxMpService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
                wxTemplateMessage.setStatus("SUCCESS");
                wxTemplateMessageMapper.updateById(wxTemplateMessage);
            } catch (WxErrorException e) {
                e.printStackTrace();
            }
        }
    }


    private void saveWxTemplateMessage(WxMpTemplateMessage wxMpTemplateMessage, String status) {
        try {
            WxTemplateMessage wxTemplateMessage = new WxTemplateMessage();
            wxTemplateMessage.setTemplateId(wxMpTemplateMessage.getTemplateId());
            wxTemplateMessage.setUrl(wxMpTemplateMessage.getUrl());
            wxTemplateMessage.setOpenId(wxMpTemplateMessage.getToUser());
            String data = WxMpGsonBuilder.create().toJson(wxMpTemplateMessage.getData());
            wxTemplateMessage.setData(data);
            wxTemplateMessage.setStatus(status);
            wxTemplateMessageMapper.insert(wxTemplateMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
