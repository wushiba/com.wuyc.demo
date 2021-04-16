package com.yfshop.shop.controller.login;

import cn.dev33.satoken.stp.StpUtil;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.exception.Asserts;
import com.yfshop.shop.config.WxStpLogic;
import com.yfshop.shop.controller.AbstractBaseController;
import com.yfshop.shop.service.user.result.UserResult;
import com.yfshop.shop.service.user.service.FrontUserService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("front/user")
public class UserLoginController extends AbstractBaseController {

    @DubboReference
    private FrontUserService frontUserService;

    @Autowired
    WxMpService wxMpService;

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


    @RequestMapping(value = "/checkSubscribe", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<Integer> checkSubscribe() throws WxErrorException {
        String openId = getCurrentOpenId();
        Asserts.assertNonNull(openId, 500, "微信未授权!");
        WxMpUser wxMpUser = wxMpService.getUserService().userInfo(openId);
        UserResult userResult = frontUserService.getUserByOpenId(openId);
        StpUtil.setLoginId(userResult.getId());
        return CommonResult.success(wxMpUser.getSubscribe() ? 1 : 0);
    }
}
