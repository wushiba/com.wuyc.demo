package com.yfshop.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.service.merchant.MerchantInfoService;
import com.yfshop.admin.api.service.merchant.result.MerchantResult;
import com.yfshop.admin.api.website.req.WebsiteCodeApplyReq1;
import com.yfshop.admin.api.website.req.WebsiteCodeApplyStatusReq;
import com.yfshop.admin.api.website.req.WebsiteCodeBindReq;
import com.yfshop.admin.api.website.req.WebsiteCodeReq;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailResult;
import com.yfshop.admin.api.website.result.WebsiteCodeResult;
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
    @RequestMapping(value = "/websiteInfo", method = {RequestMethod.POST})
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
    @RequestMapping(value = "/websiteCode", method = {RequestMethod.POST})
    public CommonResult<List<WebsiteCodeDetailResult>> getWebsiteCode() {
        List<WebsiteCodeDetailResult> websiteCodeDetailResults = merchantInfoService.getWebsiteCode(getCurrentAdminUserId());
        return CommonResult.success(websiteCodeDetailResults);
    }


    /**
     * 获取我的网点码
     *
     * @return
     */
    @SaCheckLogin
    @RequestMapping(value = "/myWebsiteCode", method = {RequestMethod.POST})
    public CommonResult<List<WebsiteCodeDetailResult>> getMyWebsiteCode(WebsiteCodeReq websiteCodeReq) {
        List<WebsiteCodeDetailResult> websiteCodeDetailResults = merchantInfoService.getMyWebsiteCode(getCurrentAdminUserId(), websiteCodeReq.getStatus(), websiteCodeReq.getDateTime());
        return CommonResult.success(websiteCodeDetailResults);
    }

    /**
     * 绑点网点码
     *
     * @return
     */
    @RequestMapping(value = "/websiteCodeBind", method = {RequestMethod.POST})
    public CommonResult<Void> websiteCodeBind(WebsiteCodeBindReq websiteReq) {
        if (StpUtil.isLogin()) {
            websiteReq.setId(StpUtil.getLoginIdAsInt());
        }
        return CommonResult.success(merchantInfoService.websiteCodeBind(websiteReq));
    }

    /**
     * 获取网点类型
     *
     * @return
     */
    @RequestMapping(value = "/websiteType", method = {RequestMethod.POST})
    public CommonResult<List<WebsiteTypeResult>> getWebsiteType() {
        List<WebsiteTypeResult> websiteTypeResults = merchantInfoService.getWebsiteType();
        return CommonResult.success(websiteTypeResults);
    }


    /**
     * 申请网点码状态
     *
     * @return
     */
    @RequestMapping(value = "/applyWebsiteCodeStatus", method = {RequestMethod.POST})
    public CommonResult<IPage<WebsiteCodeResult>> applyWebsiteCodeStatus(WebsiteCodeApplyStatusReq websiteCodeApplyReq) {
        IPage<WebsiteCodeResult> websiteTypeResults = merchantInfoService.applyWebsiteCodeStatus(getCurrentAdminUserId(), websiteCodeApplyReq.getStatus(), websiteCodeApplyReq.getPageIndex(), websiteCodeApplyReq.getPageSize());
        return CommonResult.success(websiteTypeResults);
    }


    /**
     * 更新申请网点码状态
     *
     * @return
     */
    @RequestMapping(value = "/updateApplyWebsiteCode", method = {RequestMethod.POST})
    public CommonResult<Void> updateApplyWebsiteCode(WebsiteCodeApplyStatusReq websiteCodeApplyReq) {
        merchantInfoService.updateApplyWebsiteCode(websiteCodeApplyReq.getId(), websiteCodeApplyReq.getStatus());
        return CommonResult.success(null);
    }


    /**
     * 申请网点码
     *
     * @return
     */
    @RequestMapping(value = "/applyWebsiteCode", method = {RequestMethod.POST})
    public CommonResult<Void> applyWebsiteCode(WebsiteCodeApplyReq1 websiteCodeApplyReq) {
        merchantInfoService.applyWebsiteCode(getCurrentAdminUserId(), websiteCodeApplyReq.getCount(), websiteCodeApplyReq.getEmail());
        return CommonResult.success(null);
    }


}