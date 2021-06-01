package com.yfshop.admin.controller.website;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.website.AdminWebsiteCodeManageService;
import com.yfshop.admin.api.website.request.WebsiteCodeQueryDetailsReq;
import com.yfshop.admin.api.website.request.WebsiteCodeExpressReq;
import com.yfshop.admin.api.website.request.WebsiteCodeQueryReq;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailExport;
import com.yfshop.admin.api.website.result.WebsiteCodeResult;
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
 * @author Xulg
 * Created in 2021-03-25 18:46
 */
@RestController
@RequestMapping("admin/website")
@Validated
public class AdminWebsiteManageController implements BaseController {

    @DubboReference(check = false)
    private AdminWebsiteCodeManageService adminWebsiteCodeManageService;

    @SaCheckLogin
    @ApiOperation(value = "查询网点码", httpMethod = "POST")
    @RequestMapping(value = "/queryWebsiteCode", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<IPage> queryWebsiteCode(WebsiteCodeQueryReq websiteCodeQueryReq) {
        //websiteCodeQueryReq.setMerchantId(getCurrentAdminUserId());
        return CommonResult.success(adminWebsiteCodeManageService.queryWebsiteCodeList(websiteCodeQueryReq));
    }

    @SaCheckLogin
    @ApiOperation(value = "查询全部的网点码", httpMethod = "POST")
    @RequestMapping(value = "/queryAllWebsiteCode", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<IPage> queryAllWebsiteCode(WebsiteCodeQueryReq websiteCodeQueryReq) {
        return CommonResult.success(adminWebsiteCodeManageService.queryWebsiteCodeList(websiteCodeQueryReq));
    }


    @SaCheckLogin
    @ApiOperation(value = "查询全部的网点码", httpMethod = "POST")
    @RequestMapping(value = "/queryWebsiteCodeByOlderWl", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<IPage> queryWebsiteCodeByOlderWl(WebsiteCodeQueryReq websiteCodeQueryReq) {
        return CommonResult.success(adminWebsiteCodeManageService.queryWebsiteCodeByOlderWl(websiteCodeQueryReq));
    }


    @SaCheckLogin
    @ApiOperation(value = "查询全部的网点码", httpMethod = "POST")
    @RequestMapping(value = "/queryWebsiteCodeByWl", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<IPage> queryWebsiteCodeByWl(WebsiteCodeQueryReq websiteCodeQueryReq) {
        return CommonResult.success(adminWebsiteCodeManageService.queryWebsiteCodeByWl(websiteCodeQueryReq));
    }



    @SneakyThrows
    @SaCheckLogin
    @ApiOperation(value = "关闭订单", httpMethod = "POST")
    @RequestMapping(value = "/closeWebsiteCode", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<Void> closeWebsiteCode(Integer id) {
        return CommonResult.success(adminWebsiteCodeManageService.closeWebsiteCode(id));
    }


    @SaCheckLogin
    @ApiOperation(value = "查询全部的网点码", httpMethod = "POST")
    @RequestMapping(value = "/queryWebsiteCodeDetailsByWl", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<List<WebsiteCodeResult>> queryWebsiteDetailsCodeByWl(String orderNo) {
        return CommonResult.success(adminWebsiteCodeManageService.queryWebsiteDetailsCodeByWl(orderNo));
    }


    @SaCheckLogin
    @ApiOperation(value = "查询网点码详情", httpMethod = "POST")
    @RequestMapping(value = "/queryWebsiteCodeDetails", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<IPage> queryWebsiteCodeDetailsList(WebsiteCodeQueryDetailsReq websiteCodeQueryDetailsReq) {
        return CommonResult.success(adminWebsiteCodeManageService.queryWebsiteCodeDetailsList(websiteCodeQueryDetailsReq));
    }

    @SaCheckLogin
    @ApiOperation(value = "获取网点码文件", httpMethod = "POST")
    @RequestMapping(value = "/getWebsiteCodeUrl", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<String> getWebsiteCodeUrl(Integer id) {
        return CommonResult.success(adminWebsiteCodeManageService.getWebsiteCodeUrl(id));
    }

    @SaCheckLogin
    @ApiOperation(value = "更新网点码更新物流", httpMethod = "POST")
    @RequestMapping(value = "/updateWebsiteCodeExpress", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<Void> updateWebsiteCodeExpress(WebsiteCodeExpressReq websiteCodeQueryExpressReq) {
        return CommonResult.success(adminWebsiteCodeManageService.updateWebsiteCodeExpress(websiteCodeQueryExpressReq));
    }

    @SaCheckLogin
    @ApiOperation(value = "更新网点码更新物流", httpMethod = "POST")
    @RequestMapping(value = "/updateWebsiteCodeGroupExpress", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<Void> updateWebsiteCodeGroupExpress(WebsiteCodeExpressReq websiteCodeQueryExpressReq) {
        return CommonResult.success(adminWebsiteCodeManageService.updateWebsiteCodeGroupExpress(websiteCodeQueryExpressReq));
    }






    @SaCheckLogin
    @SneakyThrows
    @ApiOperation(value = "导出网点码详情", httpMethod = "POST")
    @RequestMapping(value = "/exportWebsiteCodeDetails", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Void exportWebsiteCodeDetails(WebsiteCodeQueryDetailsReq websiteCodeQueryDetailsReq, HttpServletResponse response) {
        websiteCodeQueryDetailsReq.setMerchantId(10356);
        List<WebsiteCodeDetailExport> exportList = adminWebsiteCodeManageService.exportWebsiteCodeDetails(websiteCodeQueryDetailsReq);
        Workbook writer = ExcelExportUtil.exportExcel(new ExportParams("网点码详情详细", "网点码详情详细"), WebsiteCodeDetailExport.class, exportList);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        String name = "网点码详情详细";
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(name, "UTF-8") + ".xls");
        try (ServletOutputStream out = response.getOutputStream()) {
            writer.write(out);
            IoUtil.close(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        return null;
    }


    @SaCheckLogin
    @ApiOperation(value = "重新生成网点码", httpMethod = "POST")
    @RequestMapping(value = "/retryWebsiteCode", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<Void> retryWebsiteCode(Integer websiteCodeId) {
        return CommonResult.success(adminWebsiteCodeManageService.retryWebsiteCode(websiteCodeId));
    }
}
