package com.yfshop.open.controller;

import com.yfshop.common.base.BaseController;
import com.yfshop.common.exception.Asserts;
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
public class BlpShopController implements BaseController {

    @DubboReference
    private OrderService orderService;

    /**
     * 下载订单
     *
     * @param orderReq
     * @return
     */
    @RequestMapping(value = "/getOrder", method = {RequestMethod.POST})
    public OrderResult getOrder(OrderReq orderReq) {
        checkWhiteIp();
        return orderService.getOrder(orderReq);
    }

    /**
     * 发货信息
     *
     * @param sendReq
     * @return
     */
    @RequestMapping(value = "/send", method = {RequestMethod.POST})
    public SendResult send(SendReq sendReq) {
        checkWhiteIp();
        return orderService.send(sendReq);
    }

    /**
     * 校验退款状态
     *
     * @param checkRefundStatusReq
     * @return
     */
    @RequestMapping(value = "/checkRefundStatus", method = {RequestMethod.POST})
    public CheckRefundStatusResult checkRefundStatus(CheckRefundStatusReq checkRefundStatusReq) {
        checkWhiteIp();
        return orderService.checkRefundStatus(checkRefundStatusReq);
    }

    @RequestMapping(value = "/downloadProduct", method = {RequestMethod.POST})
    public DownloadProductResult downloadProduct(DownloadProductReq downloadProductReq) {
        checkWhiteIp();
        return orderService.downloadProduct(downloadProductReq);
    }

    @RequestMapping(value = "/syncStock", method = {RequestMethod.POST})
    public SyncStockResult syncStock(SyncStockReq syncStockReq) {
        checkWhiteIp();
        return orderService.syncStock(syncStockReq);
    }

    @RequestMapping(value = "/getRefund", method = {RequestMethod.POST})
    public RefundResult getRefund(RefundReq refundReq) {
        checkWhiteIp();
        return orderService.getRefund(refundReq);
    }


    public void checkWhiteIp() {
       // Asserts.assertTrue("192.168.1.1".contains(getRequestIpStr()), 500, "非法的ip请求");
    }
}
