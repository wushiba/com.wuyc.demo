package com.yfshop.admin.controller.merchant;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.service.merchant.AdminMerchantManageService;
import com.yfshop.admin.api.service.merchant.req.CreateMerchantReq;
import com.yfshop.admin.api.service.merchant.req.QueryMerchantReq;
import com.yfshop.admin.api.service.merchant.req.UpdateMerchantReq;
import com.yfshop.admin.api.service.merchant.result.MerchantResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotNull;

/**
 * @author Xulg
 * Created in 2021-03-25 17:22
 */
@Validated
@Controller
@RequestMapping("admin/merchant")
public class AdminMerchantManageController implements BaseController {

    @DubboReference(check = false)
    private AdminMerchantManageService adminMerchantManageService;

    @ApiOperation(value = "创建商户", httpMethod = "GET")
    @RequestMapping(value = "/createMerchant", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> createMerchant(@NotNull(message = "创建商户信息不能为空") CreateMerchantReq req) {
        return CommonResult.success(adminMerchantManageService.createMerchant(req));
    }

    @ApiOperation(value = "编辑商户", httpMethod = "GET")
    @RequestMapping(value = "/updateMerchant", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> updateMerchant(@NotNull(message = "编辑商户信息不能为空") UpdateMerchantReq req) {
        return CommonResult.success(adminMerchantManageService.updateMerchant(req));
    }

    @ApiOperation(value = "分页查询商户列表", httpMethod = "GET")
    @RequestMapping(value = "/pageQueryMerchants", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<MerchantResult>> pageQueryMerchants(QueryMerchantReq req) {
        return CommonResult.success(adminMerchantManageService.pageQueryMerchants(req));
    }

    @ApiOperation(value = "禁用商户", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "merchantId", value = "商户ID", required = true)
    })
    @RequestMapping(value = "/disableMerchant", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> disableMerchant(@NotNull(message = "商户ID不能为空") Integer merchantId) {
        return CommonResult.success(adminMerchantManageService.updateMerchantIsEnable(merchantId, false));
    }

    @ApiOperation(value = "启用商户", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "merchantId", value = "商户ID", required = true)
    })
    @RequestMapping(value = "/enableMerchant", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> enableMerchant(@NotNull(message = "商户ID不能为空") Integer merchantId) {
        return CommonResult.success(adminMerchantManageService.updateMerchantIsEnable(merchantId, true));
    }

}
