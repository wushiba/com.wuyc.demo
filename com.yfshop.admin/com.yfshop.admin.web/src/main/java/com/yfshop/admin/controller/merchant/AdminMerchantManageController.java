package com.yfshop.admin.controller.merchant;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.merchant.*;
import com.yfshop.admin.api.merchant.MerchantExcel;
import com.yfshop.admin.api.merchant.request.CreateMerchantReq;
import com.yfshop.admin.api.merchant.request.QueryMerchantReq;
import com.yfshop.admin.api.merchant.request.UpdateMerchantReq;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.common.util.ExcelUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

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
    @SaCheckRole(value = {"zb", "fgs", "sq", "jxs", "ywy", "fxs",}, mode = SaMode.OR)
    public CommonResult<Void> createMerchant(@Valid @NotNull(message = "创建商户信息不能为空") CreateMerchantReq req) {

        return CommonResult.success(adminMerchantManageService.createMerchant(getCurrentAdminUserId(), req));
    }

    @ApiOperation(value = "编辑商户", httpMethod = "GET")
    @RequestMapping(value = "/updateMerchant", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = {"zb", "fgs", "sq", "jxs", "ywy", "fxs",}, mode = SaMode.OR)
    public CommonResult<Void> updateMerchant(@Valid @NotNull(message = "编辑商户信息不能为空") UpdateMerchantReq req) {
        return CommonResult.success(adminMerchantManageService.updateMerchant(getCurrentAdminUserId(), req));
    }

    @ApiOperation(value = "分页查询商户列表", httpMethod = "GET")
    @RequestMapping(value = "/pageQueryMerchants", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = {"zb", "fgs", "sq", "jxs", "ywy", "fxs",}, mode = SaMode.OR)
    public CommonResult<IPage<MerchantResult>> pageQueryMerchants(QueryMerchantReq req) {
        return CommonResult.success(adminMerchantManageService.pageQueryMerchants(getCurrentAdminUserId(), req));
    }

    @ApiOperation(value = "条件导出商户列表", httpMethod = "GET")
    @RequestMapping(value = "/downloadMerchants", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = {"zb", "fgs", "sq", "jxs", "ywy", "fxs",}, mode = SaMode.OR)
    public void downloadMerchants(QueryMerchantReq req) {
        req.setPageSize(Integer.MAX_VALUE);
        IPage<MerchantResult> page = adminMerchantManageService.pageQueryMerchants(getCurrentAdminUserId(), req);
        List<MerchantExcel> data = page.getRecords().stream().map(m -> BeanUtil.convert(m, MerchantExcel.class))
                .collect(Collectors.toList());
        ExcelUtils.exportExcel(data, "商户信息", "商户信息",
                MerchantExcel.class, "商户信息.xls", getCurrentResponse());
    }

    @ApiOperation(value = "禁用商户", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "merchantId", value = "商户ID", required = true)
    })
    @RequestMapping(value = "/disableMerchant", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = {"zb", "fgs", "sq", "jxs", "ywy", "fxs",}, mode = SaMode.OR)
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
    @SaCheckRole(value = {"zb", "fgs", "sq", "jxs", "ywy", "fxs",}, mode = SaMode.OR)
    public CommonResult<Void> enableMerchant(@NotNull(message = "商户ID不能为空") Integer merchantId) {
        return CommonResult.success(adminMerchantManageService.updateMerchantIsEnable(merchantId, true));
    }

    @ApiOperation(value = "根据pid和角色分页查询商户", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "pageIndex", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页显示个数", required = false),
            @ApiImplicitParam(paramType = "query", name = "roleAlias", value = "角色", required = false),
            @ApiImplicitParam(paramType = "query", name = "merchantName", value = "商户名称", required = false),
    })
    @RequestMapping(value = "/pageQueryMerchantsByPidAndRoleAlias", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = {"zb", "fgs", "sq", "jxs", "ywy", "fxs",}, mode = SaMode.OR)
    public CommonResult<IPage<MerchantResult>> pageQueryMerchantsByPidAndRoleAlias(@RequestParam(name = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                                                                   @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                                                   String roleAlias, String merchantName) {
        return CommonResult.success(adminMerchantManageService.pageQueryMerchantsByPidAndRoleAlias(
                getCurrentAdminUserId(), roleAlias, merchantName, pageIndex, pageSize));
    }

}
