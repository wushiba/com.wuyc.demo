package com.yfshop.shop.controller.healthy;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.shop.service.healthy.HealthyService;
import com.yfshop.shop.service.healthy.req.PreviewShowShipPlansReq;
import com.yfshop.shop.service.healthy.req.QueryHealthyOrdersReq;
import com.yfshop.shop.service.healthy.req.SubmitHealthyOrderReq;
import com.yfshop.shop.service.healthy.result.HealthyActResult;
import com.yfshop.shop.service.healthy.result.HealthyItemResult;
import com.yfshop.shop.service.healthy.result.HealthyOrderResult;
import com.yfshop.shop.service.healthy.result.HealthySubOrderResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Xulg
 * Description:
 * Created in 2021-05-26 15:34
 */
@Validated
@Controller
@RequestMapping("front/healthy")
public class HealthyController implements BaseController {

    @DubboReference(check = false)
    private HealthyService healthyService;

    @RequestMapping(value = "/submitOrder", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<WxPayMpOrderResult> submitOrder(@Valid @NotNull(message = "提交订单信息不能为空") SubmitHealthyOrderReq req) {
        return CommonResult.success(healthyService.submitOrder(getCurrentUserId(), req));
    }

    @RequestMapping(value = "/queryHealthyItems", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<List<HealthyItemResult>> queryHealthyItems() {
        return CommonResult.success(healthyService.queryHealthyItems());
    }

    @RequestMapping(value = "/queryHealthyActivities", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<List<HealthyActResult>> queryHealthyActivities() {
        return CommonResult.success(healthyService.queryHealthyActivities());
    }

    @RequestMapping(value = "/queryHealthyActivityDetail", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<HealthyActResult> queryHealthyActivityDetail(Integer id) {
        return CommonResult.success(healthyService.queryHealthyActivityDetail(id));
    }

    @RequestMapping(value = "/pageQueryUserHealthyOrders", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<IPage<HealthyOrderResult>> pageQueryUserHealthyOrders(QueryHealthyOrdersReq req) {
        req.setUserId(getCurrentUserId());
        return CommonResult.success(healthyService.pageQueryUserHealthyOrders(req));
    }

    @RequestMapping(value = "/pageQueryHealthyOrderDetail", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<List<HealthySubOrderResult>> pageQueryHealthyOrderDetail(@NotNull(message = "订单ID不能为空") Long orderId) {
        return CommonResult.success(healthyService.pageQueryHealthyOrderDetail(getCurrentUserId(), orderId));
    }

    @RequestMapping(value = "/findHealthyItemDetail", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<HealthyItemResult> findHealthyItemDetail(@NotNull(message = "商品ID不能为空") Integer itemId) {
        return CommonResult.success(healthyService.findHealthyItemDetail(itemId));
    }

    @RequestMapping(value = "/previewShowShipPlans", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<List<String>> previewShowShipPlans(@Valid @NotNull(message = "商品不能为空") PreviewShowShipPlansReq req) {
        return CommonResult.success(healthyService.previewShowShipPlans(req).stream().map(d -> DateTime.of(d).toDateStr()).collect(Collectors.toList()));
    }
}
