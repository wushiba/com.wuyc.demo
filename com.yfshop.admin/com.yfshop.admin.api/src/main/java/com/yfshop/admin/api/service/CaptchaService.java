package com.yfshop.admin.api.service;

import com.yfshop.common.enums.CaptchaSourceEnum;
import com.yfshop.common.exception.ApiException;

public interface CaptchaService {

    /**
     * 发送验证码
     * @param mobile
     * @param captchaSourceEnum
     * @return
     * @throws ApiException
     */
    Void sendCaptcha(String mobile, CaptchaSourceEnum captchaSourceEnum) throws ApiException;

    /**
     * 校验验证码
     * @param mobile
     * @param captcha
     * @param captchaSourceEnum
     * @return
     * @throws ApiException
     */
    Void checkCaptcha(String mobile, String captcha, CaptchaSourceEnum captchaSourceEnum) throws ApiException;
}
