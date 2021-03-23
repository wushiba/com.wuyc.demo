package com.yfshop.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import com.yfshop.admin.api.enums.CaptchaSourceEnum;
import com.yfshop.admin.api.service.CaptchaService;
import com.yfshop.admin.api.service.merchant.MerchantInfoService;
import com.yfshop.admin.api.service.merchant.MerchantLoginService;
import com.yfshop.admin.api.service.merchant.result.MerchantResult;
import com.yfshop.admin.api.website.result.WebsiteBillDayResult;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailResult;
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
import java.util.List;

@Validated
@RestController
@RequestMapping("merchant/info")
class MerchantInfoController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MerchantInfoController.class);

    @DubboReference(check = false)
    private MerchantInfoService merchantInfoService;


    /**
     * 获取网点用户信息
     *
     * @return
     */
    @SaCheckLogin
    @RequestMapping(value = "/websiteInfo", method = {RequestMethod.GET})
    public CommonResult<MerchantResult> getWebsiteInfo() {
        MerchantResult merchantResult = merchantInfoService.getWebsiteInfo(getCurrentAdminUserId().intValue());
        return CommonResult.success(merchantResult);
    }

    /**
     * 获取网点码
     * @return
     */
    @SaCheckLogin
    @RequestMapping(value = "/websiteCode", method = {RequestMethod.GET})
    public CommonResult<List<WebsiteCodeDetailResult>> getWebsiteCode() {
        List<WebsiteCodeDetailResult> websiteCodeDetailResults = merchantInfoService.getWebsiteCode(getCurrentAdminUserId().intValue());
        return CommonResult.success(websiteCodeDetailResults);
    }


}