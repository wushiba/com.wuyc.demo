package com.yfshop.admin.controller;

import com.yfshop.admin.api.merchant.MerchantInfoService;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("front/website")
public class FrontWebsiteController implements BaseController {
    @DubboReference(check = false)
    private MerchantInfoService merchantInfoService;

    @RequestMapping(value = "checkActivate", method = {RequestMethod.POST})
    @ResponseBody
    public CommonResult<Integer> checkActivate(String websiteCode) {
        return CommonResult.success(merchantInfoService.checkActivate(websiteCode));
    }
}
