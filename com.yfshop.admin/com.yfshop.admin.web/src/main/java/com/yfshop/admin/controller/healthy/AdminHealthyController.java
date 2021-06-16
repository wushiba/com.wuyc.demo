package com.yfshop.admin.controller.healthy;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.yfshop.admin.api.healthy.AdminHealthyExportService;
import com.yfshop.admin.api.healthy.AdminHealthyService;
import com.yfshop.admin.api.healthy.request.HealthyActReq;
import com.yfshop.admin.api.healthy.request.HealthyItemReq;
import com.yfshop.admin.api.healthy.request.HealthySubOrderImportReq;
import com.yfshop.admin.api.healthy.request.QueryHealthyOrderReq;
import com.yfshop.admin.api.healthy.request.QueryHealthySubOrderReq;
import com.yfshop.admin.api.healthy.request.QueryJxsMerchantReq;
import com.yfshop.admin.api.healthy.request.SubOrderPostWay;
import com.yfshop.admin.api.healthy.result.HealthyActResult;
import com.yfshop.admin.api.healthy.result.HealthyItemResult;
import com.yfshop.admin.api.healthy.result.HealthyOrderDetailResult;
import com.yfshop.admin.api.healthy.result.HealthyOrderResult;
import com.yfshop.admin.api.healthy.result.HealthySubOrderExportResult;
import com.yfshop.admin.api.healthy.result.HealthySubOrderResult;
import com.yfshop.admin.api.healthy.result.JxsMerchantResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.util.ExcelUtils;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("admin/healthy")
public class AdminHealthyController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminHealthyController.class);

    @DubboReference
    private AdminHealthyService adminHealthyService;

    @DubboReference
    private AdminHealthyExportService adminHealthyExportService;

    @RequestMapping(value = "/findOrderList", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<HealthyOrderResult>> findOrderList(QueryHealthyOrderReq req) {
        return CommonResult.success(adminHealthyService.findOrderList(req));
    }


    @RequestMapping(value = "/getOrderDetail", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<HealthyOrderDetailResult> getOrderDetail(Long id) {
        return CommonResult.success(adminHealthyService.getOrderDetail(id));
    }


    @RequestMapping(value = "/findSubOrderList", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<HealthySubOrderResult>> findSubOrderList(QueryHealthySubOrderReq req) {
        return CommonResult.success(adminHealthyService.findSubOrderList(req));
    }

    @SneakyThrows
    @RequestMapping(value = "/exportSubOrderList", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public void exportSubOrderList(QueryHealthySubOrderReq req) {
        ExcelUtils.exportExcel(adminHealthyExportService.exportSubOrderList(req), "孝心订订单详情", "孝心订订单详情",
                HealthySubOrderExportResult.class, "孝心订订单详情.xls", getCurrentResponse());
    }

    @CrossOrigin
    @RequestMapping(value = "/importSubOrderList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = {"sys"}, mode = SaMode.OR)
    public CommonResult<Void> importSubOrderList(MultipartFile file) {
        List<HealthySubOrderImportReq> healthySubOrderImport = ExcelUtils.importExcel(file, 0, 1, HealthySubOrderImportReq.class);
        return CommonResult.success(adminHealthyExportService.importSubOrderList(healthySubOrderImport));
    }


    @RequestMapping(value = "/updateSubOrderPostWay", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> updateSubOrderPostWay(@RequestBody SubOrderPostWay req) {
        return CommonResult.success(adminHealthyService.updateSubOrderPostWay(req));
    }


    @RequestMapping(value = "/addAct", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> addActImage(HealthyActReq req) {
        return CommonResult.success(adminHealthyService.addAct(req));
    }


    @RequestMapping(value = "/deleteAct", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> deleteAct(Integer id) {
        return CommonResult.success(adminHealthyService.deleteAct(id));
    }


    @RequestMapping(value = "/getActList", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<HealthyActResult>> getActList(HealthyActReq req) {
        return CommonResult.success(adminHealthyService.getActList(req));
    }

    @RequestMapping(value = "/getActDetail", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<HealthyActResult> getActDetail(Integer id) {
        return CommonResult.success(adminHealthyService.getActDetail(id));
    }


    @RequestMapping(value = "/updateAct", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> updateAct(HealthyActReq req) {
        return CommonResult.success(adminHealthyService.updateAct(req));
    }

    @RequestMapping(value = "/addItem", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> addItem(@RequestBody HealthyItemReq req) {
        return CommonResult.success(adminHealthyService.addItem(req));
    }


    @RequestMapping(value = "/getItemList", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<HealthyItemResult>> getItemList(HealthyItemReq req) {
        return CommonResult.success(adminHealthyService.getItemList(req));
    }


    @RequestMapping(value = "/getItemDetail", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<HealthyItemResult> getItemDetail(Integer id) {
        return CommonResult.success(adminHealthyService.getItemDetail(id));
    }


    @RequestMapping(value = "/updateItem", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> updateItem(@RequestBody HealthyItemReq req) {
        return CommonResult.success(adminHealthyService.updateItem(req));
    }


    @RequestMapping(value = "/findJxsMerchant", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<JxsMerchantResult>> findJxsMerchant(QueryJxsMerchantReq req) {
        return CommonResult.success(adminHealthyService.findJxsMerchant(req));
    }


    @RequestMapping(value = "/closedOrder", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> closedOrder(Long id) throws WxPayException {
        return CommonResult.success(adminHealthyService.closedOrder(id));
    }
}
