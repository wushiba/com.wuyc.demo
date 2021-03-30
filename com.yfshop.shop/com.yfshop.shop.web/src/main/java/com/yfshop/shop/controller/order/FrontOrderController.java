package com.yfshop.shop.controller.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.shop.service.merchant.result.MerchantResult;
import com.yfshop.shop.service.merchant.service.FrontMerchantService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Validated
@RequestMapping("front/user")
public class FrontOrderController implements BaseController {

    @DubboReference(check = false)
    private FrontMerchantService frontMerchantService;

    @RequestMapping(value = "/findNearMerchantList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<List<MerchantResult>> findNearMerchantList(Double longitude, Double latitude) {
        return CommonResult.success(frontMerchantService.findNearMerchantList(longitude, latitude));
    }


}
