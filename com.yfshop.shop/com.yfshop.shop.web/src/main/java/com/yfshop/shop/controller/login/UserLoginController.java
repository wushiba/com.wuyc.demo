package com.yfshop.shop.controller.login;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.exception.Asserts;
import com.yfshop.shop.config.WxStpLogic;
import com.yfshop.shop.service.user.result.UserResult;
import com.yfshop.shop.service.user.service.FrontUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequestMapping("front/user")
public class UserLoginController implements BaseController {
    private static WxStpLogic wxStpLogic = new WxStpLogic();

    @DubboReference
    private FrontUserService frontUserService;

    @RequestMapping(value = "/loginByWx", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<UserResult> loginByWx() {
        Asserts.assertTrue(wxStpLogic.isLogin(), 500, "微信未授权！");
        return CommonResult.success(frontUserService.getUserByOpenId(wxStpLogic.getLoginIdAsString()));
    }

}
