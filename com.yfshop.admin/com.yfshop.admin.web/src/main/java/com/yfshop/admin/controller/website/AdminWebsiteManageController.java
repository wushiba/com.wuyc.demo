package com.yfshop.admin.controller.website;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.website.AdminWebsiteCodeManageService;
import com.yfshop.admin.api.website.req.WebsiteCodeQueryDetailsReq;
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
    public CommonResult<IPage> queryWebsiteCode(WebsiteCodeQueryReq websiteCodeQueryReq) {
        websiteCodeQueryReq.setMerchantId(10356);
        return CommonResult.success(adminWebsiteCodeManageService.queryWebsiteCodeList(websiteCodeQueryReq));
    }

    @ApiOperation(value = "查询网点码详情", httpMethod = "POST")
    @RequestMapping(value = "/queryWebsiteCodeDetails", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<IPage> queryWebsiteTypes(WebsiteCodeQueryDetailsReq websiteCodeQueryDetailsReq) {
        websiteCodeQueryDetailsReq.setMerchantId(10356);
        return CommonResult.success(adminWebsiteCodeManageService.queryWebsiteCodeDetailsList(websiteCodeQueryDetailsReq));
    }
}
