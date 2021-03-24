package com.yfshop.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.yfshop.admin.api.service.CaptchaService;
import com.yfshop.admin.api.service.merchant.MerchantLoginService;
import com.yfshop.admin.api.service.merchant.req.MerchantCaptchaReq;
import com.yfshop.admin.api.service.merchant.req.MerchantLoginReq;
import com.yfshop.admin.api.service.merchant.result.MerchantResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.enums.CaptchaSourceEnum;
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
class MerchantLoginController implements BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MerchantLoginController.class);

    @DubboReference(check = false)
    private MerchantLoginService merchantLoginService;

    @DubboReference(check = false)
    private CaptchaService captchaService;

    /**
     * 密码登录
     *
     * @return
     */
    @RequestMapping(value = "/loginByPwd", method = {RequestMethod.POST})
    public CommonResult<MerchantResult> loginByPwd(MerchantLoginReq merchantLoginReq) {
        MerchantResult merchantResult = merchantLoginService.loginByPwd(merchantLoginReq.getMobile(), merchantLoginReq.getPwd());
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
        StpUtil.setLoginId(merchantResult.getId());
        return CommonResult.success(merchantResult);
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