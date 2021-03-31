package com.yfshop.open.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.yfshop.common.api.CommonResult;
import com.yfshop.open.api.blpshop.request.OrderReq;
import com.yfshop.open.api.blpshop.result.OrderResult;
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

    @RequestMapping(value = "/getOrder", method = {RequestMethod.POST})
    public OrderResult getOrder(@RequestBody OrderReq OrderReq) {
        return orderService.getOrder(OrderReq);
    }


}
