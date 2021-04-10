package com.yfshop.admin.service.wx;

import com.yfshop.admin.api.merchant.MerchantInfoService;
import com.yfshop.admin.api.order.service.AdminUserOrderService;
import com.yfshop.common.enums.PayPrefixEnum;
import com.yfshop.wx.api.request.WxPayOrderNotifyReq;
import com.yfshop.wx.api.service.MpPayNotifyService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class MpPayNotifyServiceImpl implements MpPayNotifyService {

    @Autowired
    private MerchantInfoService merchantInfoService;

    @Autowired
    private AdminUserOrderService adminUserOrderService;

    @Override
    public void payOrderNotify(String bizType, WxPayOrderNotifyReq notifyResult) {
        PayPrefixEnum byBizType = PayPrefixEnum.getByBizType(bizType);

        switch (byBizType) {
            case WEBSITE_CODE:
                merchantInfoService.websitePayOrderNotify(notifyResult);
                break;
            case USER_ORDER:
                String outTradeNo = notifyResult.getOutTradeNo();
                String[] split = outTradeNo.split("-");
                adminUserOrderService.updateOrderPayStatus(Long.valueOf(split[1]));
                break;
        }
    }

    public static void main(String[] args) {
        String outTradeNo = "userOrder-1095-1";
        String[] split = outTradeNo.split("-");
        System.out.println(Long.valueOf(split[1]));
    }

}
