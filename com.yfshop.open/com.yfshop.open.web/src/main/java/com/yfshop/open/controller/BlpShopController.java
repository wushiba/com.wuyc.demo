package com.yfshop.open.controller;

import com.yfshop.open.api.blpshop.request.*;
import com.yfshop.open.api.blpshop.result.*;
import com.yfshop.open.api.blpshop.service.OrderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("open/blpshop")
@Validated
public class BlpShopController {

    @DubboReference
    private OrderService orderService;

    /**
     * 下载订单
     *
     * @param orderReq
     * @return
     */
    @RequestMapping(value = "/getOrder", method = {RequestMethod.POST})
    public OrderResult getOrder(@RequestBody OrderReq orderReq) {
        return orderService.getOrder(orderReq);
    }

    /**
     * 发货信息
     *
     * @param sendReq
     * @return
     */
    @RequestMapping(value = "/send", method = {RequestMethod.POST})
    public SendResult send(@RequestBody SendReq sendReq) {
        return orderService.send(sendReq);
    }

    /**
     * 校验退款状态
     *
     * @param checkRefundStatusReq
     * @return
     */
    @RequestMapping(value = "/checkRefundStatus", method = {RequestMethod.POST})
    public CheckRefundStatusResult checkRefundStatus(@RequestBody CheckRefundStatusReq checkRefundStatusReq) {
        return orderService.checkRefundStatus(checkRefundStatusReq);
    }

    @RequestMapping(value = "/downloadProduct", method = {RequestMethod.POST})
    public DownloadProductResult downloadProduct(@RequestBody DownloadProductReq downloadProductReq) {
        return orderService.downloadProduct(downloadProductReq);
    }

    @RequestMapping(value = "/getRefund", method = {RequestMethod.POST})
    public SyncStockResult syncStock(@RequestBody SyncStockReq syncStockReq) {
        return orderService.syncStock(syncStockReq);
    }

    @RequestMapping(value = "/getRefund", method = {RequestMethod.POST})
    public RefundResult getRefund(@RequestBody RefundReq refundReq) {
        return orderService.getRefund(refundReq);
    }
}
