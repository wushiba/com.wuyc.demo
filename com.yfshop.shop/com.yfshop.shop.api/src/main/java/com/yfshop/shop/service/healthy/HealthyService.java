package com.yfshop.shop.service.healthy;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.healthy.req.SubmitHealthyOrderReq;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Xulg
 * Description: TODO 请加入类描述信息
 * Created in 2021-05-26 15:51
 */
public interface HealthyService {

    WxPayMpOrderResult submitOrder(@NotNull(message = "用户ID不能为空") Integer userId,
                                   @Valid @NotNull SubmitHealthyOrderReq req) throws ApiException;

    Void notifyByWechatPay(@NotBlank(message = "订单ID不能为空") String orderNo,
                           @NotBlank(message = "支付流水号不能为空") String wechatBillNo) throws ApiException;
}
