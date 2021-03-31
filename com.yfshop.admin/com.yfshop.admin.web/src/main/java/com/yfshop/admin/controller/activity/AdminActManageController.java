package com.yfshop.admin.controller.activity;

import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.activity.request.ActCodeQueryReq;
import com.yfshop.admin.api.activity.service.AdminActCodeManageService;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.exception.Asserts;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.util.ArrayList;
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


    @ApiOperation(value = "查询活动码", httpMethod = "POST")
    @RequestMapping(value = "/queryActCode", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<IPage> queryActCode(ActCodeQueryReq actCodeQueryReq) {
        return CommonResult.success(adminActCodeManageService.queryActCodeList(actCodeQueryReq));
    }


    @SneakyThrows
    @ApiOperation(value = "导入溯源码文件", httpMethod = "POST")
    @RequestMapping(value = "/actCodeImport", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<Void> actCodeImport(Integer actId, @RequestParam("file") MultipartFile file) {
        Asserts.assertNonNull(file, 500, "请选择你要上传的溯源码文件");
        String type = file.getContentType();
        String name = file.getName();
        logger.info("文件名{}，文件类型{}", type, name);
        String md5 = SecureUtil.md5(file.getInputStream());
        adminActCodeManageService.checkFile(md5);
        BufferedReader bufferedReader = IoUtil.getUtf8Reader(file.getInputStream());
        List<String> sourceCodes = new ArrayList<>();
        bufferedReader.lines().forEach(item -> {
            Asserts.assertTrue(item.length() == 16, 500, item + "溯源码格式有误！");
            sourceCodes.add(item);

        });
        return CommonResult.success(adminActCodeManageService.actCodeImport(actId, md5, sourceCodes));
    }


    @ApiOperation(value = "获取网点码文件", httpMethod = "POST")
    @RequestMapping(value = "/actCodeUrl", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<String> actCodeUrl(Integer id) {
        return CommonResult.success(adminActCodeManageService.actCodeUrl(getCurrentAdminUserId(),id));
    }

    @ApiOperation(value = "发送网点码文件", httpMethod = "POST")
    @RequestMapping(value = "/sendEmailActCode", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<Void> sendEmailActCode(Integer id,Integer factoryId) {
        return CommonResult.success(adminActCodeManageService.sendEmailActCode(getCurrentAdminUserId(),id,factoryId));
    }


}
