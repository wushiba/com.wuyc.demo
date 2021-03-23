package com.yfshop.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import com.yfshop.admin.api.mall.AdminMallManageService;
import com.yfshop.admin.api.website.WebsiteBillService;
import com.yfshop.admin.api.website.result.WebsiteBillDayResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.validate.annotation.Mobile;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
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
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<WebsiteBillDayResult> getBillByDay(String status,Date dateTime) {
        return CommonResult.success(websiteBillService.getBillListByMerchantId(getCurrentAdminUserId().intValue(),dateTime,status));
    }


    @RequestMapping(value = "/getBillByWebsiteCode", method = {RequestMethod.GET})
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<WebsiteBillDayResult> getBillByWebsiteCode(String websiteCode,Date dateTime) {
        return CommonResult.success(websiteBillService.getBillByWebsiteCode(getCurrentAdminUserId().intValue(),websiteCode,dateTime));
    }

    @RequestMapping(value = "/billConfirm", method = {RequestMethod.GET})
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<Void> billConfirm(List<Long> ids) {
        return CommonResult.success(websiteBillService.billConfirm(getCurrentAdminUserId().intValue(),ids));
    }

    @RequestMapping(value = "/billConfirm", method = {RequestMethod.GET})
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<Void> billAllConfirm() {
        return CommonResult.success(websiteBillService.billAllConfirm(getCurrentAdminUserId().intValue()));
    }

}