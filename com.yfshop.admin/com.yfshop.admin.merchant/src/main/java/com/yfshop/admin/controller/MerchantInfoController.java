package com.yfshop.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.yfshop.admin.api.service.merchant.MerchantInfoService;
import com.yfshop.admin.api.service.merchant.result.MerchantResult;
import com.yfshop.admin.api.website.req.WebsiteReq;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailResult;
import com.yfshop.admin.api.website.result.WebsiteTypeResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
        MerchantResult merchantResult = merchantInfoService.getWebsiteInfo(getCurrentAdminUserId());
        return CommonResult.success(merchantResult);
    }


    /**
     * 获取网点码
     *
     * @return
     */
    @SaCheckLogin
    @RequestMapping(value = "/websiteCode", method = {RequestMethod.GET})
    public CommonResult<List<WebsiteCodeDetailResult>> getWebsiteCode() {
        List<WebsiteCodeDetailResult> websiteCodeDetailResults = merchantInfoService.getWebsiteCode(getCurrentAdminUserId());
        return CommonResult.success(websiteCodeDetailResults);
    }



    /**
     * 绑点网点码
     *
     * @return
     */
    @RequestMapping(value = "/websiteCodeBind", method = {RequestMethod.POST})
    public CommonResult<Void> websiteCodeBind(WebsiteReq websiteReq) {
        if (StpUtil.isLogin()){
            websiteReq.setId(StpUtil.getLoginIdAsInt());
        }
        return CommonResult.success(merchantInfoService.websiteCodeBind(websiteReq));
    }

    /**
     * 获取网点类型
     *
     * @return
     */
    @RequestMapping(value = "/websiteType", method = {RequestMethod.GET})
    public CommonResult<List<WebsiteTypeResult>> getWebsiteType() {
        List<WebsiteTypeResult> websiteTypeResults = merchantInfoService.getWebsiteType();
        return CommonResult.success(websiteTypeResults);
    }



}