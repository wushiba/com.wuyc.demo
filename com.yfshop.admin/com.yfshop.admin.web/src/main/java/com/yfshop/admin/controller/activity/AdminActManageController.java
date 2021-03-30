package com.yfshop.admin.controller.activity;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.activity.request.ActCodeQueryReq;
import com.yfshop.admin.api.activity.service.AdminActCodeManageService;
import com.yfshop.admin.api.website.request.WebsiteCodeExpressReq;
import com.yfshop.admin.api.website.request.WebsiteCodeQueryDetailsReq;
import com.yfshop.admin.api.website.request.WebsiteCodeQueryReq;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailExport;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author youshenghui
 * Created in 2021-03-25 18:46
 */
@RestController
@RequestMapping("admin/activity")
@Validated
public class AdminActManageController implements BaseController {

    @DubboReference(check = false)
    private AdminActCodeManageService adminActCodeManageService;


    @ApiOperation(value = "查询活动码", httpMethod = "POST")
    @RequestMapping(value = "/queryActCode", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<IPage> queryActCode(ActCodeQueryReq actCodeQueryReq) {
        return CommonResult.success(adminActCodeManageService.queryActCodeList(actCodeQueryReq));
    }


//    @ApiOperation(value = "导入溯源码文件", httpMethod = "POST")
//    @RequestMapping(value = "/actCodeImport", method = {RequestMethod.GET, RequestMethod.POST})
//    @ResponseBody
//    public CommonResult<Void> actCodeImport(Integer id) {
//        return CommonResult.success(adminActCodeManageService.actCodeImport(id));
//    }

}
