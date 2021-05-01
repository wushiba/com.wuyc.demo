package com.yfshop.admin.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yfshop.admin.api.website.WebsiteBillService;
import com.yfshop.admin.api.website.request.WebsiteCodeReq;
import com.yfshop.admin.api.website.result.WebsiteBillDayResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("merchant/bill")
class MerchantBillController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MerchantBillController.class);

    @DubboReference(check = false)
    private WebsiteBillService websiteBillService;

    @SaCheckLogin
    @RequestMapping(value = "/getBillByDay", method = {RequestMethod.POST})
    public CommonResult<WebsiteBillDayResult> getBillByDay(WebsiteCodeReq websiteReq) {
        return CommonResult.success(websiteBillService.getBillListByMerchantId(getCurrentAdminUserId(), websiteReq.getStartTime(), websiteReq.getEndTime(),websiteReq.getStatus()));
    }

    @SaCheckLogin
    @RequestMapping(value = "/getBillByWebsiteCode", method = {RequestMethod.POST})
    public CommonResult<WebsiteBillDayResult> getBillByWebsiteCode(WebsiteCodeReq websiteReq) {
        return CommonResult.success(websiteBillService.getBillByWebsiteCode(websiteReq.getWebsiteCode(), websiteReq.getStartTime(),websiteReq.getEndTime()));
    }

    @SaCheckLogin
    @RequestMapping(value = "/billConfirm", method = {RequestMethod.POST})
    public CommonResult<Void> billConfirm(@RequestBody List<Long> ids) {
        return CommonResult.success(websiteBillService.billConfirm(getCurrentAdminUserId(), ids));
    }

    @SaCheckLogin
    @RequestMapping(value = "/billAllConfirm", method = {RequestMethod.POST})
    public CommonResult<Void> billAllConfirm() {
        return CommonResult.success(websiteBillService.billAllConfirm(getCurrentAdminUserId()));
    }
}