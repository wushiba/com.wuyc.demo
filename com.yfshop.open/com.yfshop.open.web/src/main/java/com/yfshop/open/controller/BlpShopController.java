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
     * @return
     */
    @RequestMapping(value = "/api", method = {RequestMethod.POST})
    public Object api(ApiReq apiReq) {
        checkWhiteIp();
        Asserts.assertTrue(apiReq.checkSign(), 500, "签名校验失败！");
        switch (apiReq.getMethod()) {
            case "Differ.JH.Business.GetOrder":
                return orderService.getOrder(apiReq.getReq(OrderReq.class));
            case "Differ.JH.Business.CheckRefundStatus":
                return orderService.checkRefundStatus(apiReq.getReq(CheckRefundStatusReq.class));
            case "Differ.JH.Business.Send":
                return orderService.send(apiReq.getReq(SendReq.class));
            case "Differ.JH.Business.DownloadProduct":
                return orderService.downloadProduct(apiReq.getReq(DownloadProductReq.class));
            case "Differ.JH.Business.GetRefund":
                return orderService.getRefund(apiReq.getReq(RefundReq.class));
        }
        Asserts.assertTrue(false, 500, "没找到对应的接口方法！");
        return null;
    }

//    /**
//     * 下载订单
//     *
//     * @param orderReq
//     * @return
//     */
//    @RequestMapping(value = "/getOrder", method = {RequestMethod.POST})
//    public OrderResult getOrder(OrderReq orderReq) {
//        checkWhiteIp();
//        return orderService.getOrder(orderReq);
//    }
//
//    /**
//     * 发货信息
//     *
//     * @param sendReq
//     * @return
//     */
//    @RequestMapping(value = "/send", method = {RequestMethod.POST})
//    public SendResult send(SendReq sendReq) {
//        checkWhiteIp();
//        return orderService.send(sendReq);
//    }
//
//    /**
//     * 校验退款状态
//     *
//     * @param checkRefundStatusReq
//     * @return
//     */
//    @RequestMapping(value = "/checkRefundStatus", method = {RequestMethod.POST})
//    public CheckRefundStatusResult checkRefundStatus(CheckRefundStatusReq checkRefundStatusReq) {
//        checkWhiteIp();
//        return orderService.checkRefundStatus(checkRefundStatusReq);
//    }
//
//    @RequestMapping(value = "/downloadProduct", method = {RequestMethod.POST})
//    public DownloadProductResult downloadProduct(DownloadProductReq downloadProductReq) {
//        checkWhiteIp();
//        return orderService.downloadProduct(downloadProductReq);
//    }
//
//    @RequestMapping(value = "/syncStock", method = {RequestMethod.POST})
//    public SyncStockResult syncStock(SyncStockReq syncStockReq) {
//        checkWhiteIp();
//        return orderService.syncStock(syncStockReq);
//    }
//
//    @RequestMapping(value = "/getRefund", method = {RequestMethod.POST})
//    public RefundResult getRefund(RefundReq refundReq) {
//        checkWhiteIp();
//        return orderService.getRefund(refundReq);
//    }


    public void checkWhiteIp() {
        String whiteIp = "100.127.194.32,39.100.98.192,39.100.101.196,39.100.95.210,39.100.102.131,39.100.77.163,39.100.96.221,47.92.131.145,39.100.116.151,39.100.114.12,39.100.128.210,39.100.126.12,39.100.128.189,39.99.148.62";
        Asserts.assertTrue(whiteIp.contains(getRequestIpStr()), 500, "非法的ip请求");
    }
}
