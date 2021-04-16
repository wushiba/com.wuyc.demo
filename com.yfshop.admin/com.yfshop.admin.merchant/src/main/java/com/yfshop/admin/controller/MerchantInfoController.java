package com.yfshop.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.yfshop.admin.api.merchant.MerchantInfoService;
import com.yfshop.admin.api.merchant.request.MerchantGroupReq;
import com.yfshop.admin.api.merchant.request.MerchantReq;
import com.yfshop.admin.api.merchant.result.MerchantGroupResult;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.admin.api.user.UserService;
import com.yfshop.admin.api.user.result.UserResult;
import com.yfshop.admin.api.website.request.*;
import com.yfshop.admin.api.website.result.*;
import com.yfshop.admin.config.UserStpLogic;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.exception.Asserts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("merchant/info")
class MerchantInfoController extends AbstractBaseController {
    private static final Logger logger = LoggerFactory.getLogger(MerchantInfoController.class);

    @DubboReference(check = false)
    private MerchantInfoService merchantInfoService;

    @DubboReference(check = false)
    private UserService userService;

    public static StpLogic userLogic = new UserStpLogic();

    @Value("${websiteCode.url}")
    private String websiteCodeUrl;
    @Autowired
    WxMpService wxService;


    @SaCheckLogin
    @RequestMapping(value = "/saveMerchant", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<Void> saveMerchant(MerchantReq merchantReq) {
        merchantReq.setPId(getCurrentAdminUserId());
        return CommonResult.success(merchantInfoService.save(merchantReq));
    }

    @SaCheckLogin
    @RequestMapping(value = "/getChildMerchant", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<List<MerchantResult>> getChildMerchant() {
        return CommonResult.success(merchantInfoService.getChildMerchant(getCurrentAdminUserId()));
    }

    @RequestMapping(value = "/checkSubscribe", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<Integer> checkSubscribe() throws WxErrorException {
        String openId = getCurrentOpenId();
        Asserts.assertNonNull(openId, 500, "微信未授权!");
        WxMpUser wxMpUser = wxService.getUserService().userInfo(openId);
        return CommonResult.success(wxMpUser.getSubscribe() ? 1 : 0);
    }

    /**
     * 获取网点用户信息
     *
     * @return
     */
    @SaCheckLogin
    @RequestMapping(value = "/websiteInfo", method = {RequestMethod.POST})
    public CommonResult<MerchantResult> getWebsiteInfo() {
        MerchantResult merchantResult = merchantInfoService.getWebsiteInfo(getCurrentAdminUserId());
        String openId = merchantResult.getOpenId();
        if (StringUtils.isBlank(openId)) {
            openId = getCurrentOpenId();
        }
        if (StringUtils.isBlank(merchantResult.getHeadImgUrl()) && StringUtils.isNotBlank(openId)) {
            UserResult userResult = userService.getUserByOpenId(openId);
            if (userResult != null) {
                merchantResult.setHeadImgUrl(userResult.getHeadImgUrl());
            }
        }
        return CommonResult.success(merchantResult);
    }


    /**
     * 获取商户组
     *
     * @return
     */
    @SaCheckLogin
    @RequestMapping(value = "/merchantGroup", method = {RequestMethod.POST})
    public CommonResult<MerchantGroupResult> merchantGroup(MerchantGroupReq merchantGroupReq) {
        if (merchantGroupReq.getMerchantId() == null) {
            merchantGroupReq.setMerchantId(getCurrentAdminUserId());
        }
        MerchantGroupResult merchantGroupResult = merchantInfoService.merchantGroup(merchantGroupReq);
        return CommonResult.success(merchantGroupResult);
    }


    /**
     * 校验网点码逻辑
     *
     * @return
     */
    @RequestMapping(value = "/checkWebsiteCode", method = {RequestMethod.POST})
    public CommonResult<Integer> checkWebsiteCode(String websiteCode) {
        Integer result = merchantInfoService.checkWebsiteCode(websiteCode);
        MerchantResult merchantResult;
        if (result == 0) {
            if (StpUtil.isLogin()) {
                merchantResult = merchantInfoService.getWebsiteInfo(getCurrentAdminUserId());
            } else {
                merchantResult = merchantInfoService.getMerchantByOpenId(getCurrentOpenId());
            }
            if (merchantResult != null) {
                Asserts.assertEquals(merchantResult.getRoleAlias(), GroupRoleEnum.WD.getCode(), 500, "网点码未激活！");
            }
        } else if (result > 0) {
            merchantResult = merchantInfoService.getMerchantByWebsiteCode(websiteCode);
            result = 2;
            if (merchantResult != null) {
                if (StringUtils.isNotBlank(merchantResult.getOpenId())) {
                    if (merchantResult.getOpenId().equals(getCurrentOpenId())) {
                        //跳转商户管理
                        result = 1;
                    }
                } else if (StpUtil.isLogin() && merchantResult.getId().equals(getCurrentAdminUserId())) {
                    //跳转商户管理
                    result = 1;
                }
            }
            if (result == 1) {
                StpUtil.setLoginId(merchantResult.getId());
            } else {
                UserResult userResult = userService.getUserByOpenId(getCurrentOpenId());
                if (userResult != null) {
                    userLogic.setLoginId(userResult.getId());
                }
            }
        }
        return CommonResult.success(result);
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
        websiteReq.setOpenId(getCurrentOpenId());
        MerchantResult merchantResult = merchantInfoService.websiteCodeBind(websiteReq);
        if (merchantResult != null) {
            StpUtil.setLoginId(merchantResult.getId());
        } else {
            logger.info("绑定失败!");
        }
        return CommonResult.success(null);
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
    @SaCheckLogin
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
    @SaCheckLogin
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
    @SaCheckLogin
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
    @SaCheckLogin
    @RequestMapping(value = "/applyWebsiteCode", method = {RequestMethod.POST})
    public CommonResult<Integer> applyWebsiteCode(WebsiteCodeApplyReq websiteCodeApplyReq) {
        Integer id = merchantInfoService.applyWebsiteCode(getCurrentAdminUserId(), websiteCodeApplyReq.getCount(), websiteCodeApplyReq.getEmail());
        return CommonResult.success(id);
    }

    @SaCheckLogin
    @RequestMapping(value = "/applyWebsiteCodeAmount", method = {RequestMethod.POST})
    public CommonResult<WebsiteCodeAmountResult> applyWebsiteCodeAmount(@RequestBody List<Integer> ids) {
        WebsiteCodeAmountResult websiteCodeAmountResult = merchantInfoService.applyWebsiteCodeAmount(ids);
        return CommonResult.success(websiteCodeAmountResult);
    }

    @SaCheckLogin
    @RequestMapping(value = "/applyWebsiteCodePay", method = {RequestMethod.POST})
    public CommonResult<WxPayMpOrderResult> applyWebsiteCodePay(@RequestBody WebsiteCodePayReq websiteCodePayReq) {
        String openId = getCurrentOpenId();
        Asserts.assertNonNull(openId, 500, "需要微信授权");
        websiteCodePayReq.setOpenId(getCurrentOpenId());
        websiteCodePayReq.setUserId(getRequestIpStr());
        WxPayMpOrderResult wxPayMpOrderResult = merchantInfoService.applyWebsiteCodePay(websiteCodePayReq);
        return CommonResult.success(wxPayMpOrderResult);
    }

    /**
     * 创建网点码收货地址
     *
     * @return
     */
    @SaCheckLogin
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
    @SaCheckLogin
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
    @SaCheckLogin
    @RequestMapping(value = "/deleteWebsiteCodeAddress", method = {RequestMethod.POST})
    public CommonResult<Void> deleteWebsiteCodeAddress(Integer id) {
        merchantInfoService.deleteWebsiteCodeAddress(id);
        return CommonResult.success(null);
    }

    @SaCheckLogin
    @RequestMapping(value = "/getWebsiteCodeQrCode", method = {RequestMethod.GET})
    public String getWebsiteCodeQrCode(String websiteCode) {
        QrConfig qrConfig = new QrConfig(750, 750);
        return QrCodeUtil.generateAsBase64(websiteCodeUrl + websiteCode, qrConfig, "png");
    }


}