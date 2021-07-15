package com.yfshop.admin.controller.push;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.yfshop.admin.api.coupon.service.AdminUserCouponService;
import com.yfshop.admin.api.order.request.OrderExpressReq;
import com.yfshop.admin.api.order.request.QueryOrderReq;
import com.yfshop.admin.api.order.result.OrderDetailResult;
import com.yfshop.admin.api.order.result.OrderExportResult;
import com.yfshop.admin.api.order.result.OrderResult;
import com.yfshop.admin.api.order.service.AdminUserOrderExportService;
import com.yfshop.admin.api.order.service.AdminUserOrderService;
import com.yfshop.admin.api.push.WxPushTaskService;
import com.yfshop.admin.api.push.request.WxPushTaskReq;
import com.yfshop.admin.api.push.result.WxPushFailExportResult;
import com.yfshop.admin.api.push.result.WxPushTaskStatsResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.util.ExcelUtils;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("admin/push")
public class AdminWxPushTaskController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminWxPushTaskController.class);

    @DubboReference(check = false)
    private WxPushTaskService wxPushTaskService;

    @RequestMapping(value = "/createPushTask", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> createPushTask(WxPushTaskReq req) {
        return CommonResult.success(wxPushTaskService.createPushTask(req));
    }


    @RequestMapping(value = "/closePushTask", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> closePushTask(Integer id) {
        return CommonResult.success(wxPushTaskService.closePushTask(id));
    }

    @SaCheckLogin
    @RequestMapping(value = "/editPushTask", method = {RequestMethod.POST, RequestMethod.GET})
    @SaCheckRole(value = "sys")
    @ResponseBody
    public CommonResult<Void> editPushTask(WxPushTaskReq req) {
        return CommonResult.success(wxPushTaskService.editPushTask(req));
    }


    @SaCheckLogin
    @RequestMapping(value = "/filterPushDataCount", method = {RequestMethod.POST, RequestMethod.GET})
    @SaCheckRole(value = "sys")
    @ResponseBody
    public CommonResult<Integer> filterPushDataCount(WxPushTaskReq req) {
        return CommonResult.success(wxPushTaskService.filterPushData(req));
    }

    @SaCheckLogin
    @RequestMapping(value = "/pushTaskList", method = {RequestMethod.POST, RequestMethod.GET})
    @SaCheckRole(value = "sys")
    @ResponseBody
    public CommonResult<IPage> pushTaskList(WxPushTaskReq req) {
        return CommonResult.success(wxPushTaskService.pushTaskList(req));
    }


    @SneakyThrows
    @RequestMapping(value = "/pushTaskStats", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<WxPushTaskStatsResult> pushTaskStats() {
        return CommonResult.success(wxPushTaskService.pushTaskStats());
    }

    @SneakyThrows
    @RequestMapping(value = "/export", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public Void pushFailExport(Integer id) {
        List<WxPushFailExportResult> exportList = wxPushTaskService.pushFailExport(id);
        ExcelUtils.exportExcel(exportList, "失败推送消息详情", "失败推送消息详情",
                WxPushFailExportResult.class, "失败推送消息详情.xls", getCurrentResponse());
        return null;
    }

}
