package com.yfshop.admin.controller.spread;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.push.WxPushTaskService;
import com.yfshop.admin.api.push.request.WxPushTaskReq;
import com.yfshop.admin.api.push.result.WxPushFailExportResult;
import com.yfshop.admin.api.push.result.WxPushTaskResult;
import com.yfshop.admin.api.push.result.WxPushTaskStatsResult;
import com.yfshop.admin.api.push.result.WxPushTemplateResult;
import com.yfshop.admin.api.spread.AdminSpreadService;
import com.yfshop.admin.api.spread.request.SpreadItemReq;
import com.yfshop.admin.api.spread.request.SpreadOrderReq;
import com.yfshop.admin.api.spread.request.SpreadWithdrawReq;
import com.yfshop.admin.api.spread.result.*;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.util.ExcelUtils;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Validated
@Controller
@RequestMapping("admin/spread")
public class AdminSpreadController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminSpreadController.class);

    @DubboReference(check = false)
    private AdminSpreadService adminSpreadService;

    @RequestMapping(value = "/createItem", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> createItem(SpreadItemReq req) {
        return CommonResult.success(adminSpreadService.createItem(req));
    }


    @RequestMapping(value = "/updateItem", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> updateItem(SpreadItemReq req) {
        return CommonResult.success(adminSpreadService.updateItem(req));
    }


    @RequestMapping(value = "/getItemDetail", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<SpreadItemResult> getItemDetail(Integer id) {
        return CommonResult.success(adminSpreadService.getItemDetail(id));
    }

    @RequestMapping(value = "/getItemList", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<SpreadItemResult>> getItemList(SpreadItemReq req) {
        return CommonResult.success(adminSpreadService.getItemList(req));
    }


    @RequestMapping(value = "/getOrderList", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<SpreadOrderResult>> getOrderList(SpreadOrderReq spreadOrderReq) {
        return CommonResult.success(adminSpreadService.getOrderList(spreadOrderReq));
    }


    @RequestMapping(value = "/getWithdrawList", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<SpreadWithdrawResult>> getWithdrawList(SpreadWithdrawReq spreadWithdrawReq) {
        return CommonResult.success(adminSpreadService.getWithdrawList(spreadWithdrawReq));
    }


    @RequestMapping(value = "/getSpreadStats", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<SpreadStatsResult> getSpreadStats() {
        return CommonResult.success(adminSpreadService.getSpreadStats());
    }

    @RequestMapping(value = "/tryWithdraw", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> tryWithdraw(Long id) {
        return CommonResult.success(adminSpreadService.tryWithdraw(id));
    }


    @SneakyThrows
    @RequestMapping(value = "/getOrderExport", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public Void getOrderExport(SpreadOrderReq spreadOrderReq) {
        List<SpreadOrderExport> exportList = adminSpreadService.getOrderExport(spreadOrderReq);
        ExcelUtils.exportExcel(exportList, "分销订单详情", "分销订单详情",
                SpreadOrderExport.class, "分销订单详情.xls", getCurrentResponse());
        return null;
    }


    @SneakyThrows
    @RequestMapping(value = "/getWithdrawExport", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public Void getWithdrawExport(SpreadWithdrawReq spreadWithdrawReq) {
        List<SpreadWithdrawExport> exportList = adminSpreadService.getWithdrawExport(spreadWithdrawReq);
        ExcelUtils.exportExcel(exportList, "分销提现详情", "分销提现详情",
                SpreadWithdrawExport.class, "分销提现详情.xls", getCurrentResponse());
        return null;
    }

}
