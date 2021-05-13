package com.yfshop.admin.controller.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.yfshop.admin.api.order.request.OrderExpressReq;
import com.yfshop.admin.api.order.request.QueryOrderReq;
import com.yfshop.admin.api.order.result.OrderDetailResult;
import com.yfshop.admin.api.order.result.OrderResult;
import com.yfshop.admin.api.order.service.AdminUserOrderService;
import com.yfshop.admin.api.website.request.WebsiteCodeExpressReq;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Validated
@Controller
@RequestMapping("admin/order")
public class AdminOrderManageController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminOrderManageController.class);

    @DubboReference(check = false)
    private AdminUserOrderService adminUserOrderService;


    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<OrderResult>> list(QueryOrderReq req) {
        return CommonResult.success(adminUserOrderService.list(req));
    }

    @RequestMapping(value = "/close", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> closeOrder(Long id) throws WxPayException {
        return CommonResult.success(adminUserOrderService.closeOrder(id));
    }

    @SaCheckLogin
    @ApiOperation(value = "更新订单物流", httpMethod = "POST")
    @RequestMapping(value = "/updateOrderExpress", method = {RequestMethod.POST})
    @ResponseBody
    public CommonResult<Void> updateOrderExpress(OrderExpressReq orderExpressReq) {
        return CommonResult.success(adminUserOrderService.updateOrderExpress(orderExpressReq));
    }


    @SaCheckLogin
    @ApiOperation(value = "更新订单物流", httpMethod = "POST")
    @RequestMapping(value = "/updateOrderExpress", method = {RequestMethod.POST})
    @ResponseBody
    public CommonResult<OrderDetailResult> getOrderDetail(Long id) {
        return CommonResult.success(adminUserOrderService.getOrderDetail(id));
    }

}
