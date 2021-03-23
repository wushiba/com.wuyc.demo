package com.yfshop.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import com.yfshop.admin.api.mall.request.CreateItemCategoryReq;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.validate.annotation.Mobile;
import org.hibernate.validator.constraints.Length;
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


    @RequestMapping(value = "/loginByPwd", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<Void> loginByPwd(@Mobile(message = "手机号不正确") String mobile, @NotNull(message = "密码不能为空") String pwd) {
        return CommonResult.failed();
    }

    @RequestMapping(value = "/loginByCode", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckPermission
    public CommonResult<Void> loginByCode(@Mobile(message = "手机号不正确") String mobile, @NotNull(message = "验证码不能为空") String code) {
        return CommonResult.failed();
    }

}