package com.yfshop.shop.controller.login;

import cn.dev33.satoken.stp.StpUtil;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.exception.Asserts;
import com.yfshop.shop.config.WxStpLogic;
import com.yfshop.shop.controller.AbstractBaseController;
import com.yfshop.shop.service.user.result.UserResult;
import com.yfshop.shop.service.user.service.FrontUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("front/user")
public class UserLoginController extends AbstractBaseController {

    @DubboReference
    private FrontUserService frontUserService;

    @RequestMapping(value = "/loginByWx", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<UserResult> loginByWx() {
        String openId = getCurrentOpenId();
        Asserts.assertNonNull(openId, 500, "微信未授权!");
        UserResult userResult = frontUserService.getUserByOpenId(openId);
        StpUtil.setLoginId(userResult.getId());
        return CommonResult.success(userResult);
    }

    @RequestMapping(value = "/getUserInfo", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<UserResult> getCurrUserInfo() {
        return CommonResult.success(frontUserService.getUserById(getCurrentUserId()));
    }

}
