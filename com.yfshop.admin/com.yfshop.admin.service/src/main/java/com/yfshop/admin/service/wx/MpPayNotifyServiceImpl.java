package com.yfshop.admin.service.wx;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.yfshop.admin.api.merchant.MerchantInfoService;
import com.yfshop.wx.api.service.MpPayNotifyService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class MpPayNotifyServiceImpl implements MpPayNotifyService {
    @Autowired
    private MerchantInfoService merchantInfoService;

    @Override
    public void payOrderNotify(String bizType, WxPayOrderNotifyResult notifyResult) {

        switch (bizType) {
            case "websitePay":
                merchantInfoService.websitePayOrderNotify(notifyResult);
                break;
        }

    }
}
