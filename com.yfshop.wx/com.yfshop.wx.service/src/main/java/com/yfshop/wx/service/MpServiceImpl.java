package com.yfshop.wx.service;

import java.time.LocalDateTime;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.google.gson.reflect.TypeToken;
import com.yfshop.code.mapper.OrderDetailMapper;
import com.yfshop.code.mapper.WxPayNotifyMapper;
import com.yfshop.code.mapper.WxPayRefundMapper;
import com.yfshop.code.mapper.WxTemplateMessageMapper;
import com.yfshop.code.model.OrderDetail;
import com.yfshop.code.model.WxPayNotify;
import com.yfshop.code.model.WxPayRefund;
import com.yfshop.code.model.WxTemplateMessage;
import com.yfshop.common.util.DateUtil;
import com.yfshop.wx.api.request.WxPayRefundReq;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@DubboService
public class MpServiceImpl implements MpService {
    @Autowired
    private WxMpService wxMpService;
    @Resource
    private WxTemplateMessageMapper wxTemplateMessageMapper;

    @Autowired
    private WxPayService wxService;


    @Async
    @Override
    public void sendWxMpTemplateMsg(WxMpTemplateMessage wxMpTemplateMessage) {
        try {
//            if ("o3vDm6TQEJn4BsPB3xi5p4EXvSHo,o3vDm6de43Cl-sFwyFpg78sZK22w".contains(wxMpTemplateMessage.getToUser())) {
//                return;
//            }
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
        if (wxTemplateMessage != null && "FAIL".equals(wxTemplateMessage.getStatus())) {
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void refund(WxPayRefundReq wxPayRefundReq) throws WxPayException {
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
        wxPayRefundRequest.setTransactionId(wxPayRefundReq.getTransactionId());
        wxPayRefundRequest.setRefundFee(wxPayRefundReq.getRefundFee());
        wxPayRefundRequest.setTotalFee(wxPayRefundReq.getTotalFee());
        wxPayRefundRequest.setOutTradeNo(wxPayRefundReq.getOuttradeNo());
        wxPayRefundRequest.setOutRefundNo(wxPayRefundReq.getRefundNo());
        wxPayRefundRequest.setRefundDesc("订单关闭");
        this.wxService.refund(wxPayRefundRequest);
        return null;
    }

//    @Override
//    public Void refundAll(Date startTime, Date endTime) {
//        List<WxPayNotify> wxPayNotifies = wxPayNotifyMapper.selectList(Wrappers.<WxPayNotify>lambdaQuery().between(startTime != null && endTime != null, WxPayNotify::getCreateTime, startTime, endTime));
//        wxPayNotifies.forEach(item -> {
//            WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
//            try {
//                wxPayRefundRequest.setTransactionId(item.getTransactionId());
//                wxPayRefundRequest.setRefundFee(item.getTotalFee());
//                wxPayRefundRequest.setTotalFee(item.getTotalFee());
//                wxPayRefundRequest.setOutTradeNo(item.getOuttradeNo());
//                wxPayRefundRequest.setOutRefundNo(item.getTransactionId());
//                wxPayRefundRequest.setRefundDesc("测试退款");
//                this.wxService.refund(wxPayRefundRequest);
//            } catch (WxPayException e) {
//                e.printStackTrace();
//            }
//        });
//        return null;
//    }


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
