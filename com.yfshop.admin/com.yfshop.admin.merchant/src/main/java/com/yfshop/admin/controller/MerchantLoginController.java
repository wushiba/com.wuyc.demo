package com.yfshop.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.yfshop.admin.api.merchant.MerchantInfoService;
import com.yfshop.admin.api.service.CaptchaService;
import com.yfshop.admin.api.merchant.MerchantLoginService;
import com.yfshop.admin.api.merchant.request.MerchantCaptchaReq;
import com.yfshop.admin.api.merchant.request.MerchantLoginReq;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.enums.CaptchaSourceEnum;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.validate.annotation.Mobile;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@Validated
@RestController
@RequestMapping("merchant/login")
class MerchantLoginController extends AbstractBaseController {

    private static final Logger logger = LoggerFactory.getLogger(MerchantLoginController.class);

    @DubboReference
    private MerchantLoginService merchantLoginService;

    @DubboReference
    private MerchantInfoService merchantService;

    @DubboReference
    private CaptchaService captchaService;

    /**
     * 密码登录
     *
     * @return
     */
    @RequestMapping(value = "/loginByPwd", method = {RequestMethod.POST})
    public CommonResult<MerchantResult> loginByPwd(MerchantLoginReq merchantLoginReq) {
        MerchantResult merchantResult = merchantLoginService.loginByPwd(merchantLoginReq.getMobile(), merchantLoginReq.getPwd());
        Asserts.assertTrue("jxs,fxs,cxy,ywy,cxy,wd".contains(merchantResult.getRoleAlias()), 500, "您不支持公众号登录！");
        StpUtil.setLoginId(merchantResult.getId());
        return CommonResult.success(merchantResult);
    }

    /**
     * 验证码登录
     *
     * @return
     */
    @RequestMapping(value = "/loginByCaptcha", method = {RequestMethod.POST})
    @ResponseBody
    public CommonResult<MerchantResult> loginByCaptcha(MerchantCaptchaReq merchantCaptchaReq) {
        MerchantResult merchantResult = merchantLoginService.loginByCaptcha(merchantCaptchaReq.getMobile(), merchantCaptchaReq.getCaptcha());
        Asserts.assertTrue("jxs,fxs,cxy,ywy,cxy,wd".contains(merchantResult.getRoleAlias()), 500, "您不支持公众号登录！");
        StpUtil.setLoginId(merchantResult.getId());
        return CommonResult.success(merchantResult);
    }

    @RequestMapping(value = "/loginByWx", method = {RequestMethod.POST})
    @ResponseBody
    public CommonResult<MerchantResult> loginByWx() {
        if (StpUtil.isLogin()) {
            MerchantResult merchantResult = merchantService.getWebsiteInfo(getCurrentAdminUserId());
            Asserts.assertTrue("jxs,fxs,cxy,ywy,cxy,wd".contains(merchantResult.getRoleAlias()), 500, "您不支持公众号登录！");
            StpUtil.setLoginId(merchantResult.getId());
            return CommonResult.success(merchantResult);
        } else {
            String openId = getCurrentOpenId();
            Asserts.assertStringNotBlank(openId, 605, "微信未授权");
            MerchantResult merchantResult = merchantLoginService.loginByWx(openId);
            Asserts.assertTrue("jxs,fxs,cxy,ywy,cxy,wd".contains(merchantResult.getRoleAlias()), 500, "您不支持公众号登录！");
            StpUtil.setLoginId(merchantResult.getId());
            return CommonResult.success(merchantResult);
        }
    }

    /**
     * 退出登录
     *
     * @return
     */
    @RequestMapping(value = "/logout", method = {RequestMethod.POST})
    public CommonResult<MerchantResult> logout() {
        StpUtil.logout();
        return CommonResult.success(null);
    }

    /**
     * 验证码发送
     *
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/sendCaptcha", method = {RequestMethod.POST})
    @ResponseBody
    public CommonResult<Void> sendCaptcha(@Mobile(message = "手机号不正确") String mobile) {
        return CommonResult.success(captchaService.sendCaptcha(mobile, CaptchaSourceEnum.LOGIN_CAPTCHA));
    }


}