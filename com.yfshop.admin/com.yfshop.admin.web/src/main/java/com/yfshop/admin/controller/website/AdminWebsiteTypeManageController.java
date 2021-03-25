package com.yfshop.admin.controller.website;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.yfshop.admin.api.website.AdminWebsiteTypeManageService;
import com.yfshop.admin.api.website.result.WebsiteTypeResult;
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

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-25 18:46
 */
@Controller
@RequestMapping("admin/websiteType")
@Validated
public class AdminWebsiteTypeManageController implements BaseController {

    @DubboReference(check = false)
    private AdminWebsiteTypeManageService adminWebsiteTypeManageService;

    @ApiOperation(value = "创建网点类型", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "typeName", value = "网点类型", required = true),
    })
    @RequestMapping(value = "/createWebsiteType", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> createWebsiteType(@NotBlank(message = "网点类型不能为空") String typeName) {
        return CommonResult.success(adminWebsiteTypeManageService.createWebsiteType(typeName));
    }

    @ApiOperation(value = "查询网点类型列表", httpMethod = "GET")
    @RequestMapping(value = "/queryWebsiteTypes", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<List<WebsiteTypeResult>> queryWebsiteTypes() {
        return CommonResult.success(adminWebsiteTypeManageService.queryWebsiteTypes());
    }
}
