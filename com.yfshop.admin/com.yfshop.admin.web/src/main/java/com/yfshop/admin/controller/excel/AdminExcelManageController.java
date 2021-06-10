package com.yfshop.admin.controller.excel;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.yfshop.admin.api.excel.ExcelService;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Xulg
 * Created in 2021-03-25 18:46
 */
@RestController
@RequestMapping("admin/excel")
@Validated
public class AdminExcelManageController implements BaseController {

    @DubboReference(check = false)
    private ExcelService excelService;

    @SaCheckLogin
    @RequestMapping(value = "/sendWebsiteData", method = {RequestMethod.GET, RequestMethod.POST})
    @SaCheckRole(value = "sys")
    @ResponseBody
    public CommonResult<Void> sendWebsiteData() {
        return CommonResult.success(excelService.sendWebsiteData());
    }

}
