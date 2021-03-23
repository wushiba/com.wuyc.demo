package com.yfshop.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import com.yfshop.admin.api.enums.CaptchaSourceEnum;
import com.yfshop.admin.api.service.CaptchaService;
import com.yfshop.admin.api.service.merchant.MerchantLoginService;
import com.yfshop.admin.api.service.merchant.result.MerchantResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.validate.annotation.Mobile;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Validated
@RestController
@RequestMapping("merchant/login")
class MerchantLoginController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MerchantLoginController.class);

    //@DubboReference(check = false)
    private MerchantLoginService merchantLoginService;
    @DubboReference(check = false)
    private CaptchaService captchaService;

    private final WxMpService wxService;

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
     * 微信授权登录
     *
     * @param appid
     * @param code
     * @return
     */
    @RequestMapping("/loginByCode")
    public CommonResult<Void> loginByCode(@RequestParam String appid, @RequestParam String code) {
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }
        try {
            WxOAuth2AccessToken accessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo user = wxService.getOAuth2Service().getUserInfo(accessToken, null);
            StpUtil.setLoginId(user.getOpenid());
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

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