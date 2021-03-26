package com.yfshop.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.service.merchant.MerchantInfoService;
import com.yfshop.admin.api.service.merchant.result.MerchantResult;
import com.yfshop.admin.api.website.req.*;
import com.yfshop.admin.api.website.result.*;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import io.swagger.models.auth.In;
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
@RequestMapping("merchant/info")
class MerchantInfoController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MerchantInfoController.class);

    @DubboReference(check = false)
    private MerchantInfoService merchantInfoService;
    public static StpLogic stpLogic = new StpLogic("wx");

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
        websiteReq.setOpenId(stpLogic.getLoginIdAsString());
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
     * 申请网点码详情
     *
     * @return
     */
    @RequestMapping(value = "/applyWebsiteCodeDetails", method = {RequestMethod.POST})
    public CommonResult<WebsiteCodeResult> applyWebsiteCodeDetails(Integer id) {
        WebsiteCodeResult websiteTypeResult = merchantInfoService.applyWebsiteCodeDetails(id);
        return CommonResult.success(websiteTypeResult);
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
    public CommonResult<Integer> applyWebsiteCode(WebsiteCodeApplyReq websiteCodeApplyReq) {
        Integer id=merchantInfoService.applyWebsiteCode(getCurrentAdminUserId(), websiteCodeApplyReq.getCount(), websiteCodeApplyReq.getEmail());
        return CommonResult.success(id);
    }


    @RequestMapping(value = "/applyWebsiteCodeAmount", method = {RequestMethod.POST})
    public CommonResult<WebsiteCodeAmountResult> applyWebsiteCodeAmount(@RequestBody List<Integer> ids) {
        WebsiteCodeAmountResult websiteCodeAmountResult=merchantInfoService.applyWebsiteCodeAmount(ids);
        return CommonResult.success(websiteCodeAmountResult);
    }


    @RequestMapping(value = "/applyWebsiteCodePay", method = {RequestMethod.POST})
    public CommonResult<WebsiteCodePayResult> applyWebsiteCodePay(@RequestBody WebsiteCodePayReq websiteCodePayReq) {
        WebsiteCodePayResult websiteCodePayResult=merchantInfoService.applyWebsiteCodePay(websiteCodePayReq);
        return CommonResult.success(websiteCodePayResult);
    }

    /**
     * 创建网点码收货地址
     *
     * @return
     */
    @RequestMapping(value = "/websiteCodeAddress", method = {RequestMethod.POST})
    public CommonResult<Void> websiteCodeAddress(WebsiteCodeAddressReq websiteCodeAddressReq) {
        websiteCodeAddressReq.setMerchantId(getCurrentAdminUserId());
        merchantInfoService.websiteCodeAddress(websiteCodeAddressReq);
        return CommonResult.success(null);
    }


    /**
     * 创建网点码收货地址
     *
     * @return
     */
    @RequestMapping(value = "/getWebsiteCodeAddress", method = {RequestMethod.POST})
    public CommonResult<List<WebsiteCodeAddressResult>> getWebsiteCodeAddress() {
        List<WebsiteCodeAddressResult> list = merchantInfoService.getWebsiteCodeAddress(getCurrentAdminUserId());
        return CommonResult.success(list);
    }


    /**
     * 删除网点码收货地址
     *
     * @return
     */
    @RequestMapping(value = "/deleteWebsiteCodeAddress", method = {RequestMethod.POST})
    public CommonResult<Void> deleteWebsiteCodeAddress(Integer id) {
        merchantInfoService.deleteWebsiteCodeAddress(id);
        return CommonResult.success(null);
    }
}