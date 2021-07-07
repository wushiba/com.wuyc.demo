package com.yfshop.admin.controller.order;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.yfshop.admin.api.coupon.service.AdminUserCouponService;
import com.yfshop.admin.api.draw.request.QueryDrawRecordExportReq;
import com.yfshop.admin.api.draw.result.DrawRecordExportResult;
import com.yfshop.admin.api.order.request.OrderExpressReq;
import com.yfshop.admin.api.order.request.QueryOrderReq;
import com.yfshop.admin.api.order.result.OrderDetailResult;
import com.yfshop.admin.api.order.result.OrderExportResult;
import com.yfshop.admin.api.order.result.OrderResult;
import com.yfshop.admin.api.order.service.AdminUserOrderExportService;
import com.yfshop.admin.api.order.service.AdminUserOrderService;
import com.yfshop.admin.api.website.request.WebsiteCodeExpressReq;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

@Validated
@Controller
@RequestMapping("admin/order")
public class AdminOrderManageController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminOrderManageController.class);

    @DubboReference(check = false)
    private AdminUserOrderService adminUserOrderService;

    @DubboReference(check = false)
    private AdminUserOrderExportService adminUserOrderExportService;

    @DubboReference(check = false)
    private AdminUserCouponService adminUserCouponService;

    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<OrderResult>> list(QueryOrderReq req) {
        return CommonResult.success(adminUserOrderService.list(req));
    }


    @RequestMapping(value = "/close", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> closeOrder(Long id) throws WxPayException {
        return CommonResult.success(adminUserOrderService.closeOrder(id));
    }

    @SaCheckLogin
    @ApiOperation(value = "更新订单物流", httpMethod = "POST")
    @RequestMapping(value = "/updateOrderExpress", method = {RequestMethod.POST})
    @SaCheckRole(value = "sys")
    @ResponseBody
    public CommonResult<Void> updateOrderExpress(OrderExpressReq orderExpressReq) {
        return CommonResult.success(adminUserOrderService.updateOrderExpress(orderExpressReq));
    }


    @SaCheckLogin
    @ApiOperation(value = "获取订单详情", httpMethod = "POST")
    @RequestMapping(value = "/getOrderDetail", method = {RequestMethod.POST})
    @SaCheckRole(value = "sys")
    @ResponseBody
    public CommonResult<OrderDetailResult> getOrderDetail(Long id) {
        return CommonResult.success(adminUserOrderService.getOrderDetail(id));
    }

    @SaCheckLogin
    @ApiOperation(value = "重新发送申通快递", httpMethod = "POST")
    @RequestMapping(value = "/trySendStoOrder", method = {RequestMethod.POST})
    @SaCheckRole(value = "sys")
    @ResponseBody
    public CommonResult<Void> trySendStoOrder(Long id) {
        return CommonResult.success(adminUserOrderService.trySendStoOrder(id));
    }

    @SneakyThrows
    @RequestMapping(value = "/export", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public Void getOrderExport(QueryOrderReq recordReq, HttpServletResponse response) {
        List<OrderExportResult> exportList = adminUserOrderExportService.orderExport(recordReq);
        Workbook writer = ExcelExportUtil.exportExcel(new ExportParams("订单记录详情", "订单记录"), DrawRecordExportResult.class, exportList);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        String name = "订单记录详情";
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


    @RequestMapping(value = "/testOrder", method = {RequestMethod.POST})
    @ResponseBody
    public CommonResult<Void> testOrder(Long orderId, String billNo) {
        if ("pro".equalsIgnoreCase(SpringUtil.getActiveProfile())) {
            return CommonResult.failed("非法接口调用");
        } else {
            return CommonResult.success(adminUserOrderService.updateOrderPayStatus(orderId, billNo));
        }
    }


    @RequestMapping(value = "/sendUserCoupon", method = {RequestMethod.POST})
    @ResponseBody
    public CommonResult<Void> sendUserCoupon(Long orderId) {
        if ("pro".equalsIgnoreCase(SpringUtil.getActiveProfile())) {
            return CommonResult.failed("非法接口调用");
        } else {
            adminUserCouponService.sendUserCoupon(orderId);
            return CommonResult.success(null);
        }
    }
}
