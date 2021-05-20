package com.yfshop.admin.controller.merchant;

import cn.dev33.satoken.stp.StpUtil;
import com.yfshop.admin.api.merchant.MerchantLoginService;
import com.yfshop.admin.api.merchant.request.MerchantLoginReq;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.exception.Asserts;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@Validated
@RestController
@RequestMapping("admin/merchant/login")
class AdminMerchantLoginController implements BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AdminMerchantLoginController.class);

    @DubboReference(check = false)
    private MerchantLoginService merchantLoginService;


    /**
     * 密码登录
     *
     * @return
     */
    @RequestMapping(value = "/loginByPwd", method = {RequestMethod.POST})
    public CommonResult<MerchantResult> loginByPwd(MerchantLoginReq merchantLoginReq) {
        MerchantResult merchantResult = merchantLoginService.loginByPwd(merchantLoginReq.getMobile(), merchantLoginReq.getPwd());
        Asserts.assertNotEquals(merchantResult.getRoleAlias(),GroupRoleEnum.WD.getCode(),500,"网点用户请在公众号登录！");
        StpUtil.setLoginId(merchantResult.getId());
        return CommonResult.success(merchantResult);
    }

    /**
     * 退出登录
     *
     * @return
     */
    @RequestMapping(value = "/logout", method = {RequestMethod.POST})
    public CommonResult<MerchantResult> logout() {
       // StpUtil.logout();
        return CommonResult.success(null);
    }


}