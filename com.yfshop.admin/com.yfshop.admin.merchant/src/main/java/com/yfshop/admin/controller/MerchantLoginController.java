package com.yfshop.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.yfshop.admin.api.service.CaptchaService;
import com.yfshop.admin.api.service.merchant.MerchantLoginService;
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

import javax.validation.constraints.NotNull;


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
     * @param mobile
     * @param pwd
     * @return
     */
    @RequestMapping(value = "/loginByPwd", method = {RequestMethod.POST})
    public CommonResult<MerchantResult> loginByPwd(@Mobile(message = "手机号不正确") String mobile, @NotNull(message = "密码不能为空") String pwd) {
        MerchantResult merchantResult = merchantLoginService.loginByPwd(mobile, pwd);
        StpUtil.setLoginId(merchantResult.getId());
        return CommonResult.success(merchantResult);
    }

    /**
     * 验证码登录
     *
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/loginByCaptcha", method = {RequestMethod.POST})
    @ResponseBody
    public CommonResult<MerchantResult> loginByCaptcha(@Mobile(message = "手机号不正确") String mobile, @NotNull(message = "验证码不能为空") String captcha) {
        MerchantResult merchantResult = merchantLoginService.loginByCaptcha(mobile, captcha);
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