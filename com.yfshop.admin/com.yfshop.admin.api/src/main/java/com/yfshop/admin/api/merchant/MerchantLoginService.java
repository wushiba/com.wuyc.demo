package com.yfshop.admin.api.merchant;

import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.common.exception.ApiException;

public interface MerchantLoginService {

    /**
     * 密码登录
     *
     * @param mobile
     * @param pwd
     * @return
     */
    MerchantResult loginByPwd(String mobile, String pwd) throws ApiException;

    /**
     * 验证码登录
     *
     * @param mobile
     * @param captcha
     * @return
     */
    MerchantResult loginByCaptcha(String mobile, String captcha) throws ApiException;
}
