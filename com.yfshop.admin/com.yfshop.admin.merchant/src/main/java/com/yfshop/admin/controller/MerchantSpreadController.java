package com.yfshop.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.extra.servlet.ServletUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.push.result.WxPushFailExportResult;
import com.yfshop.admin.api.spread.AdminSpreadService;
import com.yfshop.admin.api.spread.SpreadService;
import com.yfshop.admin.api.spread.request.*;
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

import java.math.BigDecimal;
import java.util.List;

@Validated
@Controller
@RequestMapping("merchant/spread")
public class MerchantSpreadController extends AbstractBaseController {
    private static final Logger logger = LoggerFactory.getLogger(MerchantSpreadController.class);

    @DubboReference(check = false)
    private SpreadService spreadService;


    @RequestMapping(value = "/getItemShortUrl", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<String> getItemShortUrl(Integer itemId) throws Exception {
        return CommonResult.success(spreadService.createPromotion(getCurrentAdminUserId(), itemId));
    }

    @RequestMapping(value = "/getItemList", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<List<SpreadItemResult>> getItemList() {
        return CommonResult.success(spreadService.getItemList());
    }


    @RequestMapping(value = "/getItemDetail", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<SpreadItemResult> getItemDetail(Integer id) {
        return CommonResult.success(spreadService.getItemDetail(id));
    }

    @RequestMapping(value = "/getOrderList", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<IPage<SpreadOrderResult>> getOrderList(SpreadOrderReq spreadOrderReq) {
        spreadOrderReq.setMerchantId(getCurrentAdminUserId());
        return CommonResult.success(spreadService.getOrderList(spreadOrderReq));
    }


    @RequestMapping(value = "/getSpreadStats", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<SpreadStatsResult> getSpreadStats() {
        return CommonResult.success(spreadService.getSpreadStats(getCurrentAdminUserId()));
    }

    @RequestMapping(value = "/getOrderDetail", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<SpreadOrderResult> getOrderDetail(Long id) {
        return CommonResult.success(spreadService.getOrderDetail(id));
    }

    @RequestMapping(value = "/getBalance", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<BigDecimal> getBalance(Long id) {
        return CommonResult.success(spreadService.getBalance(getCurrentAdminUserId()));
    }

    @RequestMapping(value = "/getBillList", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<IPage<SpreadBillResult>> getBillList(SpreadBillReq spreadBillReq) {
        spreadBillReq.setMerchantId(getCurrentAdminUserId());
        return CommonResult.success(spreadService.getBillList(spreadBillReq));
    }

    @RequestMapping(value = "/withDraw", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<Void> withDraw(SpreadWithdrawReq spreadWithdrawReq) {
        spreadWithdrawReq.setMerchantId(getCurrentAdminUserId());
        spreadWithdrawReq.setIpStr(ServletUtil.getClientIP(getCurrentRequest()));
        spreadWithdrawReq.setOpenId(getCurrentOpenId());
        return CommonResult.success(spreadService.withDraw(spreadWithdrawReq));
    }

    @RequestMapping(value = "/getGroupOrderList", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<IPage<SpreadGroupOrderResult>> getGroupOrderList(SpreadGroupOrderReq spreadOrderReq) {
        spreadOrderReq.setMerchantId(getCurrentAdminUserId());
        return CommonResult.success(spreadService.getGroupOrderList(spreadOrderReq));
    }


    @RequestMapping(value = "/getGroupOrderStats", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<SpreadGroupOrderStatsResult> getSpreadGroupOrderStats(SpreadGroupOrderReq spreadOrderReq) {
        spreadOrderReq.setMerchantId(getCurrentAdminUserId());
        return CommonResult.success(spreadService.getSpreadGroupOrderStats(spreadOrderReq));
    }

    @RequestMapping(value = "/getOrderStats", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<SpreadGroupOrderStatsResult> getOrderStats(SpreadGroupOrderReq spreadOrderReq) {
        spreadOrderReq.setMerchantId(getCurrentAdminUserId());
        return CommonResult.success(spreadService.getSpreadOrderStats(spreadOrderReq));
    }
}
