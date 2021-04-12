package com.yfshop.shop.controller.website;

import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.shop.service.website.FrontWebsiteService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("front/website")
public class FrontWebsiteController implements BaseController {
    @DubboReference
    FrontWebsiteService frontWebsiteService;

    @RequestMapping(value = "checkActivate", method = {RequestMethod.POST})
    @ResponseBody
    public CommonResult<Integer> checkActivate(String websiteCode) {
        return CommonResult.success(frontWebsiteService.checkActivate(websiteCode));
    }
}
