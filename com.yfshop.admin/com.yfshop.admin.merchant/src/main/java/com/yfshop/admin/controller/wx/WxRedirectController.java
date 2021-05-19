package com.yfshop.admin.controller.wx;

import cn.dev33.satoken.stp.StpUtil;
import com.yfshop.admin.api.merchant.MerchantInfoService;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.admin.api.user.UserService;
import com.yfshop.admin.api.user.request.UserReq;
import com.yfshop.admin.config.WxMpProperties;
import com.yfshop.admin.config.WxStpLogic;
import com.yfshop.admin.controller.AbstractBaseController;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.util.BeanUtil;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Edward
 */
@AllArgsConstructor
@RestController
@RequestMapping("/wx/redirect")
public class WxRedirectController extends AbstractBaseController {
    private final WxMpProperties wxMpProperties;
    private final WxMpService wxService;
    @DubboReference(check = false)
    private UserService userService;
    final static WxStpLogic wxStpLogic = new WxStpLogic();
    private static final Logger logger = LoggerFactory.getLogger(WxRedirectController.class);

    @RequestMapping("/authByCode")
    public CommonResult<MerchantResult> authByCode(@RequestParam String code) {
        String appId = wxMpProperties.getConfigs().get(0).getAppId();
        if (!this.wxService.switchover(appId)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appId));
        }
        try {
            WxOAuth2AccessToken accessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo user = wxService.getOAuth2Service().getUserInfo(accessToken, null);
            UserReq userReq = BeanUtil.convert(user, UserReq.class);
            userReq.setOpenId(user.getOpenid());
            userService.saveUser(userReq);
            wxStpLogic.setLoginId(user.getOpenid());
        } catch (WxErrorException e) {
            e.printStackTrace();
            return CommonResult.failed();
        }

        return CommonResult.success(null);
    }

}
