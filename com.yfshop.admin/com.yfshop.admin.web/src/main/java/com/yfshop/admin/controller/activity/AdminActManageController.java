package com.yfshop.admin.controller.activity;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.activity.request.ActCodeImportReq;
import com.yfshop.admin.api.activity.request.ActCodeQueryDetailsReq;
import com.yfshop.admin.api.activity.request.ActCodeQueryReq;
import com.yfshop.admin.api.activity.result.ActCodeBatchRecordResult;
import com.yfshop.admin.api.activity.service.AdminActCodeManageService;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author youshenghui
 * Created in 2021-03-25 18:46
 */
@RestController
@RequestMapping("admin/activity")
@Validated
public class AdminActManageController implements BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AdminActManageController.class);

    @DubboReference(check = false)
    private AdminActCodeManageService adminActCodeManageService;

    @SaCheckLogin
    @ApiOperation(value = "查询活动码", httpMethod = "POST")
    @RequestMapping(value = "/queryActCode", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<IPage> queryActCode(ActCodeQueryReq actCodeQueryReq) {
        return CommonResult.success(adminActCodeManageService.queryActCodeList(actCodeQueryReq));
    }


    @SaCheckLogin
    @CrossOrigin
    @SneakyThrows
    @ApiOperation(value = "导入溯源码文件", httpMethod = "POST")
    @RequestMapping(value = "/actCodeImportUrl", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<Void> actCodeImportUrl(ActCodeImportReq importReq) {

        return CommonResult.success(adminActCodeManageService.actCodeImport(importReq.getActId(), importReq.getMd5(), importReq.getUrl()));
    }

    @SaCheckLogin
    @CrossOrigin
    @SneakyThrows
    @ApiOperation(value = "导入溯源码文件", httpMethod = "POST")
    @RequestMapping(value = "/actCodeImportCount", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<Void> actCodeImportCount(Integer actId,Integer count) {

        return CommonResult.success(adminActCodeManageService.actCodeImportCount(actId, count));
    }

    @SaCheckLogin
    @ApiOperation(value = "获取网点码文件", httpMethod = "POST")
    @RequestMapping(value = "/actCodeUrl", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<String> actCodeUrl(Integer id) {
        return CommonResult.success(adminActCodeManageService.actCodeUrl(getCurrentAdminUserId(), id));
    }

    @SaCheckLogin
    @ApiOperation(value = "发送网点码文件", httpMethod = "POST")
    @RequestMapping(value = "/sendEmailActCode", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<Void> sendEmailActCode(Integer id, Integer factoryId) {
        return CommonResult.success(adminActCodeManageService.sendEmailActCode(getCurrentAdminUserId(), id, factoryId));
    }

    @SaCheckLogin
    @ApiOperation(value = "查询活动码", httpMethod = "POST")
    @RequestMapping(value = "/queryActCodeDetails", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<IPage> queryActCodeDetails(ActCodeQueryDetailsReq actCodeQueryReq) {
        return CommonResult.success(adminActCodeManageService.queryActCodeDetails(actCodeQueryReq));
    }

    @SaCheckLogin
    @ApiOperation(value = "查询活动码记录", httpMethod = "POST")
    @RequestMapping(value = "/queryActCodeDownloadRecord", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<List<ActCodeBatchRecordResult>> queryActCodeDownloadRecord(Integer batchId) {
        return CommonResult.success(adminActCodeManageService.queryActCodeDownloadRecord(batchId));
    }


}
