package com.yfshop.shop.controller.healthy;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.shop.service.healthy.HealthyService;
import com.yfshop.shop.service.healthy.req.SubmitHealthyOrderReq;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Xulg
 * Description:
 * Created in 2021-05-26 15:34
 */
@Validated
@RestController
@RequestMapping("healthy")
public class HealthyController implements BaseController {

    @DubboReference(check = false)
    private HealthyService healthyService;

    @RequestMapping(value = "/submitOrder", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<WxPayMpOrderResult> submitOrder(@Valid @NotNull(message = "提交订单信息不能为空") SubmitHealthyOrderReq req) {
        return CommonResult.success(healthyService.submitOrder(getCurrentUserId(), req));
    }

}
