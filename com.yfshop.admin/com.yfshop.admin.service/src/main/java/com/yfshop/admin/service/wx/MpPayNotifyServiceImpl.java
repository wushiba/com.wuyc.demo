package com.yfshop.admin.service.wx;

import com.yfshop.admin.api.merchant.MerchantInfoService;
import com.yfshop.admin.api.order.service.AdminUserOrderService;
import com.yfshop.code.mapper.WxPayNotifyMapper;
import com.yfshop.code.model.WxPayNotify;
import com.yfshop.common.enums.PayPrefixEnum;
import com.yfshop.wx.api.request.WxPayOrderNotifyReq;
import com.yfshop.wx.api.service.MpPayNotifyService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;

@DubboService(async = true)
@EnableAsync
public class MpPayNotifyServiceImpl implements MpPayNotifyService {

    @Autowired
    private MerchantInfoService merchantInfoService;

    @Autowired
    private AdminUserOrderService adminUserOrderService;

    @Resource
    private WxPayNotifyMapper wxPayNotifyMapper;

    @Override
    @Async
    public void payOrderNotify(String bizType, WxPayOrderNotifyReq notifyResult) {
        PayPrefixEnum byBizType = PayPrefixEnum.getByBizType(bizType);
        switch (byBizType) {
            case WEBSITE_CODE:
                merchantInfoService.websitePayOrderNotify(notifyResult);
                break;
            case USER_ORDER:
                String outTradeNo = notifyResult.getOutTradeNo();
                String billNo = notifyResult.getTransactionId();
                String[] split = outTradeNo.split("-");
                adminUserOrderService.updateOrderPayStatus(Long.valueOf(split[1]), billNo);
                break;
        }
        savePayOrderNotify(bizType, notifyResult);
    }


    private void savePayOrderNotify(String bizType, WxPayOrderNotifyReq notifyResult) {
        try {
            WxPayNotify wxPayNotify = new WxPayNotify();
            wxPayNotify.setOpenId(notifyResult.getOpenid());
            wxPayNotify.setBankType(notifyResult.getBankType());
            wxPayNotify.setTotalFee(notifyResult.getTotalFee());
            wxPayNotify.setSettlementTotalFee(notifyResult.getSettlementTotalFee());
            wxPayNotify.setCashFee(notifyResult.getCashFee());
            wxPayNotify.setTransactionId(notifyResult.getTransactionId());
            wxPayNotify.setOuttradeNo(notifyResult.getOutTradeNo());
            wxPayNotify.setTimeEnd(notifyResult.getTimeEnd());
            wxPayNotify.setNotifyUrl(bizType);
            wxPayNotifyMapper.insert(wxPayNotify);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
