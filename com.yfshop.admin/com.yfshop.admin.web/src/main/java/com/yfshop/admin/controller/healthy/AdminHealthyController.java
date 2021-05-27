package com.yfshop.admin.controller.healthy;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.healthy.AdminHealthyService;
import com.yfshop.admin.api.healthy.request.HealthyActReq;
import com.yfshop.admin.api.healthy.request.QueryHealthyOrderReq;
import com.yfshop.admin.api.healthy.request.QueryHealthySubOrderReq;
import com.yfshop.admin.api.healthy.result.HealthyOrderDetailResult;
import com.yfshop.admin.api.healthy.result.HealthyOrderResult;
import com.yfshop.admin.api.healthy.result.HealthySubOrderResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("admin/healthy")
public class AdminHealthyController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminHealthyController.class);

    @DubboReference
    private AdminHealthyService adminHealthyService;

    @RequestMapping(value = "/findOrderList", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<HealthyOrderResult>> findOrderList(QueryHealthyOrderReq req) {
        return CommonResult.success(adminHealthyService.findOrderList(req));
    }


    @RequestMapping(value = "/getOrderDetail", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<HealthyOrderDetailResult> getOrderDetail(Integer id) {
        return CommonResult.success(adminHealthyService.getOrderDetail(id));
    }


    @RequestMapping(value = "/findSubOrderList", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<HealthySubOrderResult>> findSubOrderList(QueryHealthySubOrderReq req) {
        return CommonResult.success(adminHealthyService.findSubOrderList(req));
    }


    @RequestMapping(value = "/addActImage", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> addActImage(HealthyActReq req) {
        return CommonResult.success(adminHealthyService.addAct(req));
    }

}
