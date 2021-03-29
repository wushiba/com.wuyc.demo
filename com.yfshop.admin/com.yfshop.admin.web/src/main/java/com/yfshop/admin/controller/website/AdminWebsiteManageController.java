package com.yfshop.admin.controller.website;

import com.yfshop.admin.api.website.AdminWebsiteCodeManageService;
import com.yfshop.admin.api.website.req.WebsiteCodeQueryReq;
import com.yfshop.admin.api.website.result.WebsiteCodeResult;
import com.yfshop.admin.api.website.result.WebsiteTypeResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-25 18:46
 */
@RestController
@RequestMapping("admin/website")
@Validated
public class AdminWebsiteManageController implements BaseController {

    @DubboReference(check = false)
    private AdminWebsiteCodeManageService adminWebsiteCodeManageService;


    @ApiOperation(value = "查询网点码", httpMethod = "POST")
    @RequestMapping(value = "/queryWebsiteCode", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<List<WebsiteCodeResult>> queryWebsiteTypes(WebsiteCodeQueryReq websiteCodeQueryReq) {
        websiteCodeQueryReq.setMerchantId(10358);
        return CommonResult.success(adminWebsiteCodeManageService.queryWebsiteCodeList(websiteCodeQueryReq));
    }
}
