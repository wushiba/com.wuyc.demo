package com.yfshop.admin.controller;

import com.yfshop.common.api.CommonResult;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.web.bind.annotation.PathVariable;
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
@AllArgsConstructor
@RestController
@RequestMapping("/wx/jsapi/{appid}")
public class WxJsapiController {
    private final WxMpService wxService;

    @PostMapping("/getJsapiTicket")
    public CommonResult getJsapiTicket(@PathVariable String appid, String url) throws WxErrorException {
        final WxJsapiSignature jsapiSignature = this.wxService.switchoverTo(appid).createJsapiSignature(URLDecoder.decode(url));
        System.out.println(this.wxService.getJsapiTicket(false));
        return CommonResult.success(jsapiSignature);
    }
}
