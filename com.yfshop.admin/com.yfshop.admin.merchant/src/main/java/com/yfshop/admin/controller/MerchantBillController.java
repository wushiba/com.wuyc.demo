package com.yfshop.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import com.yfshop.admin.api.website.WebsiteBillService;
import com.yfshop.admin.api.website.result.WebsiteBillDayResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Validated
@RestController
@RequestMapping("merchant/bill")
class MerchantBillController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MerchantBillController.class);

    @DubboReference(check = false)
    private WebsiteBillService websiteBillService;

    @RequestMapping(value = "/getBillByDay", method = {RequestMethod.GET})
    public CommonResult<WebsiteBillDayResult> getBillByDay(String status, Date dateTime) {
        return CommonResult.success(websiteBillService.getBillListByMerchantId(10358, dateTime, status));
    }


    @RequestMapping(value = "/getBillByWebsiteCode", method = {RequestMethod.GET})
    public CommonResult<WebsiteBillDayResult> getBillByWebsiteCode(String websiteCode, Date dateTime) {
        return CommonResult.success(websiteBillService.getBillByWebsiteCode(10358, websiteCode, dateTime));
    }

    @RequestMapping(value = "/billConfirm", method = {RequestMethod.GET})
    public CommonResult<Void> billConfirm(List<Long> ids) {
        return CommonResult.success(websiteBillService.billConfirm(10358, ids));
    }

    @RequestMapping(value = "/billAllConfirm", method = {RequestMethod.GET})
    public CommonResult<Void> billAllConfirm() {
        return CommonResult.success(websiteBillService.billAllConfirm(10358));
    }
}