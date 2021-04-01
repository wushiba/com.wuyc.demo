package com.yfshop.admin.controller.sourcefactory;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.sourcefactory.AdminSourceFactoryManageService;
import com.yfshop.admin.api.sourcefactory.excel.SourceFactoryExcel;
import com.yfshop.admin.api.sourcefactory.req.CreateSourceFactoryReq;
import com.yfshop.admin.api.sourcefactory.req.ImportSourceFactoryReq;
import com.yfshop.admin.api.sourcefactory.req.QuerySourceFactoriesReq;
import com.yfshop.admin.api.sourcefactory.req.UpdateSourceFactoryReq;
import com.yfshop.admin.api.sourcefactory.result.SourceFactoryResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.ExcelUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Xulg
 * Created in 2021-03-25 19:55
 */
@Validated
@Controller
@RequestMapping("admin/sourceFactory")
public class AdminSourceFactoryManageController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminSourceFactoryManageController.class);

    @DubboReference(check = false)
    private AdminSourceFactoryManageService adminSourceFactoryManageService;

    @ApiOperation(value = "创建工厂", httpMethod = "GET")
    @RequestMapping(value = "/createSourceFactory", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> createSourceFactory(@Valid @NotNull(message = "创建工厂信息不能为空") CreateSourceFactoryReq req) {
        return CommonResult.success(adminSourceFactoryManageService.createSourceFactory(req));
    }

    @ApiOperation(value = "编辑工厂信息", httpMethod = "GET")
    @RequestMapping(value = "/updateSourceFactory", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> updateSourceFactory(@Valid @NotNull(message = "编辑工厂信息不能为空") UpdateSourceFactoryReq req) {
        return CommonResult.success(adminSourceFactoryManageService.updateSourceFactory(req));
    }


    @ApiOperation(value = "导入工厂数据Excel文件", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "excel文件"),
    })
    @RequestMapping(value = "/importSourceFactory", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public CommonResult<Void> importSourceFactory(@NotNull(message = "导入文件不能为空") MultipartFile file) {
        List<SourceFactoryExcel> excels = ExcelUtils.importExcel(file, 1, 1, SourceFactoryExcel.class);
        Asserts.assertCollectionNotEmpty(excels, 500, "未能解析出Excel内容");
        ImportSourceFactoryReq req = ImportSourceFactoryReq.builder().excels(excels).build();
        return CommonResult.success(adminSourceFactoryManageService.importSourceFactory(req));
    }

    @ApiOperation(value = "分页查询工厂", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "pageIndex", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页显示个数", required = false),
            @ApiImplicitParam(paramType = "query", name = "factoryName", value = "工厂名称", required = false),
    })
    @RequestMapping(value = "/pageQuerySourceFactories", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<SourceFactoryResult>> pageQuerySourceFactories(QuerySourceFactoriesReq req) {
        return CommonResult.success(adminSourceFactoryManageService.pageQuerySourceFactories(req));
    }

    @ApiOperation(value = "导出工厂", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "factoryName", value = "工厂名称", required = false),
    })
    @RequestMapping(value = "/downloadSourceFactory", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public void downloadSourceFactory(QuerySourceFactoriesReq req) {
        req.setPageSize(Integer.MAX_VALUE);
        IPage<SourceFactoryResult> page = adminSourceFactoryManageService.pageQuerySourceFactories(req);
        List<SourceFactoryExcel> data = page.getRecords().stream()
                .map(sf -> {
                    SourceFactoryExcel excel = new SourceFactoryExcel();
                    excel.setFactoryName(sf.getFactoryName());
                    excel.setContacts(sf.getContacts());
                    excel.setMobile(sf.getMobile());
                    excel.setEmail(sf.getEmail());
                    excel.setProvince(sf.getProvince());
                    excel.setCity(sf.getCity());
                    excel.setDistrict(sf.getDistrict());
                    excel.setAddress(sf.getAddress());
                    excel.setIsEnable(sf.getIsEnable());
                    return excel;
                }).collect(Collectors.toList());
        ExcelUtils.exportExcel(data, "工厂信息", "工厂信息",
                SourceFactoryExcel.class, "工厂信息.xls", getCurrentResponse());
    }
}
