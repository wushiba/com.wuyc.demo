package com.yfshop.admin.controller.menu;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yfshop.admin.api.menu.AdminMenuManageService;
import com.yfshop.admin.api.menu.result.MenuResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-23 17:51
 */
@Validated
@Controller
@RequestMapping("admin/menu")
public class AdminMenuManageController implements BaseController {

    @DubboReference(check = false)
    private AdminMenuManageService adminMenuManageService;

    @ApiOperation(value = "查询商户的菜单", httpMethod = "GET")
    @RequestMapping(value = "/queryMerchantMenus", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<List<MenuResult>> queryMerchantMenus() {
        return CommonResult.success(adminMenuManageService.queryMerchantMenus(getCurrentAdminUserId()));
    }
}
