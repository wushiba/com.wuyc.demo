package com.yfshop.admin.controller.healthy;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.healthy.AdminHealthyService;
import com.yfshop.admin.api.healthy.request.*;
import com.yfshop.admin.api.healthy.result.*;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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


    @RequestMapping(value = "/updateSubOrderPostWay", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> updateSubOrderPostWay(@RequestBody SubOrderPostWay req) {
        return CommonResult.success(adminHealthyService.updateSubOrderPostWay(req));
    }


    @RequestMapping(value = "/addAct", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> addActImage(HealthyActReq req) {
        return CommonResult.success(adminHealthyService.addAct(req));
    }


    @RequestMapping(value = "/getActList", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<HealthyActResult>> getActList(HealthyActReq req) {
        return CommonResult.success(adminHealthyService.getActList(req));
    }


    @RequestMapping(value = "/updateAct", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> updateAct(HealthyActReq req) {
        return CommonResult.success(adminHealthyService.updateAct(req));
    }

    @RequestMapping(value = "/addItem", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> addItem(HealthyItemReq req) {
        return CommonResult.success(adminHealthyService.addItem(req));
    }


    @RequestMapping(value = "/getItemList", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<HealthyItemResult>> getItemList(HealthyItemReq req) {
        return CommonResult.success(adminHealthyService.getItemList(req));
    }

    @RequestMapping(value = "/updateItem", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> updateItem(HealthyItemReq req) {
        return CommonResult.success(adminHealthyService.updateItem(req));
    }


    @RequestMapping(value = "/findJxsMerchant", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<JxsMerchantResult>> findJxsMerchant(QueryJxsMerchantReq req) {
        return CommonResult.success(adminHealthyService.findJxsMerchant(req));
    }
}
