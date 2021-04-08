package com.yfshop.shop.controller.wx;


import com.yfshop.common.api.CommonResult;
import com.yfshop.shop.config.WxMpProperties;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;

/**
 * jsapi 演示接口的 com.yfshop.open.controller.
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 * @date 2020-04-25
 */

@RestController
@AllArgsConstructor
@RequestMapping("/wx/jsapi")
public class WxJsapiController {

    private final WxMpProperties wxMpProperties;
    private final WxMpService wxService;

    @PostMapping("/getJsapiTicket")
    public CommonResult getJsapiTicket(String url) throws WxErrorException {
        String appId = wxMpProperties.getConfigs().get(0).getAppId();
        final WxJsapiSignature jsapiSignature = this.wxService.switchoverTo(appId).createJsapiSignature(URLDecoder.decode(url));
        System.out.println(this.wxService.getJsapiTicket(false));
        return CommonResult.success(jsapiSignature);
    }
}
