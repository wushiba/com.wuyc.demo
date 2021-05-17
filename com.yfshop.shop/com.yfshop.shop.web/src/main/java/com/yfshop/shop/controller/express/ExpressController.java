package com.yfshop.shop.controller.express;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.shop.service.express.result.ExpressOrderResult;
import com.yfshop.shop.service.express.result.ExpressResult;
import com.yfshop.shop.service.order.service.FrontExpressService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 快递接口查询
 * Created in 2021-03-24 10:35
 */
@Controller
@RequestMapping("front/express")
@Validated
public class ExpressController implements BaseController {

    @DubboReference
    private FrontExpressService expressService;


    @RequestMapping(value = "/queryByWayBillNo", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<List<ExpressResult>> queryByWayBillNo(String wayBillNo) {

        return CommonResult.success(expressService.queryExpressByWayBillNo(wayBillNo));
    }

    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<ExpressOrderResult> query(Long id) {

        return CommonResult.success(expressService.queryExpress(id));
    }
}
