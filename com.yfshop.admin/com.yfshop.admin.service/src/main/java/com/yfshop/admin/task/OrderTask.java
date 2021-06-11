package com.yfshop.admin.task;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.yfshop.admin.api.order.service.AdminUserOrderService;
import com.yfshop.admin.api.website.WebsiteCodeTaskService;
import com.yfshop.admin.utils.ProxyUtil;
import com.yfshop.code.mapper.OrderMapper;
import com.yfshop.code.mapper.WebsiteCodeGroupMapper;
import com.yfshop.code.mapper.WebsiteCodeMapper;
import com.yfshop.code.model.Order;
import com.yfshop.code.model.WebsiteCode;
import com.yfshop.code.model.WebsiteCodeGroup;
import com.yfshop.common.enums.PayPrefixEnum;
import com.yfshop.wx.api.service.MpPayService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderTask {
    //15分钟
    static int expiredTime = 15;
    @Resource
    WebsiteCodeMapper websiteCodeMapper;
    @Resource
    OrderMapper orderMapper;
    @DubboReference
    MpPayService payService;

    @DubboReference
    private WebsiteCodeTaskService websiteCodeTask;

    @DubboReference
    private AdminUserOrderService adminUserService;

    @Resource
    private WebsiteCodeGroupMapper websiteCodeGroupMapper;

    /**
     * 同步网点码未支付的订单
     */
    public void syncWebsiteCodeOrder() {
        long currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")) / 60;
        List<WebsiteCode> websiteCodeList = websiteCodeMapper.selectList(Wrappers.<WebsiteCode>lambdaQuery().eq(WebsiteCode::getOrderStatus, "PAYING"));
        List<Integer> failIds = new ArrayList<>();
        for (WebsiteCode websiteCode : websiteCodeList) {
            long time = websiteCode.getUpdateTime().toEpochSecond(ZoneOffset.of("+8")) / 60;
            long offset = currentTime - time;
            if (offset > expiredTime) {
                failIds.add(websiteCode.getId());
                payService.closeOrder(websiteCode.getOrderNo());
            } else {
                //1,5,10,15 频率调用
                if (offset == 1 || offset == 5 || offset == 10 || offset == 15) {
                    try {
                        WxPayOrderQueryResult wxPayOrderQueryResult = payService.queryOrder(websiteCode.getOrderNo());
                        switch (wxPayOrderQueryResult.getTradeState()) {
                            case "SUCCESS":
                                //通过代理实现异步触发
                                OrderTask orderTask = ProxyUtil.getProxy(OrderTask.class);
                                orderTask.websiteCodePay(wxPayOrderQueryResult);
                                break;
                            case "REFUND":
                            case "CLOSED":
                            case "REVOKED":
                            case "PAYERROR":
                                failIds.add(websiteCode.getId());
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(failIds)) {
            WebsiteCode websiteCode = new WebsiteCode();
            websiteCode.setOrderStatus("PENDING");
            int count =websiteCodeMapper.update(websiteCode, Wrappers.<WebsiteCode>lambdaQuery()
                    .in(WebsiteCode::getId, failIds)
                    .eq(WebsiteCode::getOrderStatus, "PAYING"));
            if (count>0) {
                WebsiteCode w = websiteCodeMapper.selectById(failIds.get(0));
                if (w != null && StringUtils.isNotBlank(w.getOrderNo())) {
                    WebsiteCodeGroup websiteCodeGroup = new WebsiteCodeGroup();
                    websiteCodeGroup.setOrderStatus("CANCEL");
                    websiteCodeGroupMapper.update(websiteCodeGroup, Wrappers.<WebsiteCodeGroup>lambdaQuery()
                            .eq(WebsiteCodeGroup::getOrderNo, w.getOrderNo()));
                }
            }
        }

    }

    @Async
    public void websiteCodePay(WxPayOrderQueryResult wxPayOrderQueryResult) {
        try {
            WebsiteCode websiteCode = new WebsiteCode();
            websiteCode.setPayMethod("WxPay");
            websiteCode.setPayTime(LocalDateTime.parse(wxPayOrderQueryResult.getTimeEnd(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            websiteCode.setOrderStatus("WAIT");
            websiteCode.setBillno(wxPayOrderQueryResult.getTransactionId());
            int count = websiteCodeMapper.update(websiteCode, Wrappers.<WebsiteCode>lambdaQuery()
                    .eq(WebsiteCode::getOrderNo, wxPayOrderQueryResult.getOutTradeNo())
                    .eq(WebsiteCode::getOrderStatus, "PAYING"));
            if (count > 0) {
                WebsiteCodeGroup websiteCodeGroup = new WebsiteCodeGroup();
                websiteCodeGroup.setOrderStatus("WAIT");
                websiteCodeGroup.setPayMethod("WxPay");
                websiteCodeGroup.setPayTime(LocalDateTime.now());
                websiteCodeGroup.setBillno(wxPayOrderQueryResult.getTransactionId());
                websiteCodeGroupMapper.update(websiteCodeGroup, Wrappers.<WebsiteCodeGroup>lambdaQuery()
                        .eq(WebsiteCodeGroup::getOrderNo, wxPayOrderQueryResult.getOutTradeNo()));
                websiteCodeTask.doWorkWebsiteCodeFile(wxPayOrderQueryResult.getOutTradeNo());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步网点码未支付的订单
     */
    public void syncShopOrder() {
        long currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")) / 60;
        List<Order> orders = orderMapper.selectList(Wrappers.<Order>lambdaQuery().eq(Order::getIsPay, "I").eq(Order::getIsCancel, "N"));
        List<Long> failIds = new ArrayList<>();
        for (Order order : orders) {
            long time = order.getUpdateTime().toEpochSecond(ZoneOffset.of("+8")) / 60;
            long offset = currentTime - time;
            if (offset > expiredTime) {
                failIds.add(order.getId());
                payService.closeOrder(order.getBillNo());
            } else {
                //1,5,10,15 频率调用
                if (offset == 1 || offset == 5 || offset == 10 || offset == 15) {
                    try {
                        String outOrderId = PayPrefixEnum.USER_ORDER.getPrefix() + order.getId() + "-" + order.getPayEntryCount();
                        WxPayOrderQueryResult wxPayOrderQueryResult = payService.queryOrder(outOrderId);
                        switch (wxPayOrderQueryResult.getTradeState()) {
                            case "SUCCESS":
                                //通过代理实现异步触发
                                OrderTask orderTask = ProxyUtil.getProxy(OrderTask.class);
                                orderTask.shopPay(wxPayOrderQueryResult);
                                break;
                            case "REFUND":
                            case "CLOSED":
                            case "REVOKED":
                            case "PAYERROR":
                                failIds.add(order.getId());
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(failIds)) {
            Order order = new Order();
            order.setIsPay("N");
            orderMapper.update(order, Wrappers.<Order>lambdaQuery()
                    .in(Order::getId, failIds)
                    .eq(Order::getIsPay, "I"));
        }
    }


    @Async
    public void shopPay(WxPayOrderQueryResult wxPayOrderQueryResult) {
        try {
            String[] split = wxPayOrderQueryResult.getOutTradeNo().split("-");
            adminUserService.updateOrderPayStatus(Long.valueOf(split[1]), wxPayOrderQueryResult.getTransactionId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
