package com.yfshop.admin.controller.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yfshop.admin.api.express.ExpressService;
import com.yfshop.admin.api.express.result.ExpressOrderResult;
import com.yfshop.admin.api.express.result.ExpressResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("admin/express")
@Validated
public class ExpressController implements BaseController {
    @DubboReference
    private ExpressService expressService;


    @RequestMapping(value = "/queryByExpressNo", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<List<ExpressResult>> queryByExpressNo(String expressNo, String expressName, String receiverMobile) {

        return CommonResult.success(expressService.queryByExpressNo(expressNo, expressName, receiverMobile));
    }

    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<ExpressOrderResult> query(Long id) {

        return CommonResult.success(expressService.queryExpress(id));
    }
}
