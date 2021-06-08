package com.yfshop.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.healthy.MerchantHealthyService;
import com.yfshop.admin.api.healthy.request.PostWayHealthySubOrderReq;
import com.yfshop.admin.api.healthy.request.QueryJxsHealthySubOrderReq;
import com.yfshop.admin.api.healthy.request.QueryMerchantHealthySubOrdersReq;
import com.yfshop.admin.api.healthy.result.HealthySubOrderResult;
import com.yfshop.admin.api.merchant.request.QueryMerchantReq;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.validate.annotation.CandidateValue;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Xulg
 * Description: TODO 请加入类描述信息
 * Created in 2021-05-31 14:02
 */
@Validated
@Controller
@RequestMapping("merchant/healthy")
public class MerchantHealthyController extends AbstractBaseController {

    @DubboReference(check = false)
    private MerchantHealthyService merchantHealthyService;

    @RequestMapping(value = "/pageQueryMerchantHealthySubOrders", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = {"fxs", "ywy", "cxy"}, mode = SaMode.OR)
    public CommonResult<IPage<HealthySubOrderResult>> pageQueryMerchantHealthySubOrders(
            @RequestParam(name = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @NotBlank(message = "订单状态不能为空") @CandidateValue(candidateValue = {"ALL", "WAIT_ALLOCATE", "COMPLETE_DELIVERY"}) String orderStatus) {
        QueryMerchantHealthySubOrdersReq req = new QueryMerchantHealthySubOrdersReq();
        req.setPageIndex(pageIndex);
        req.setPageSize(pageSize);
        req.setMerchantId(getCurrentAdminUserId());
        req.setOrderStatus(orderStatus);
        return CommonResult.success(merchantHealthyService.pageQueryMerchantHealthySubOrders(req));
    }

    @RequestMapping(value = "/getJxsSubOrderList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = {"jxs"}, mode = SaMode.OR)
    public CommonResult<IPage<HealthySubOrderResult>> pageJxsSubOrderList(QueryJxsHealthySubOrderReq req) {
        req.setMerchantId(getCurrentAdminUserId());
        return CommonResult.success(merchantHealthyService.pageJxsSubOrderList(req));
    }


    @RequestMapping(value = "/getMerchantHealthyList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = {"jxs"}, mode = SaMode.OR)
    public CommonResult<IPage<MerchantResult>> pageMerchantHealthyList(QueryMerchantReq req) {
        req.setMerchantId(getCurrentAdminUserId());
        return CommonResult.success(merchantHealthyService.pageMerchantHealthyList(getRequestIpStr(),req));
    }


    @RequestMapping(value = "/updatePostWaySubOrder", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = {"jxs"}, mode = SaMode.OR)
    public CommonResult<Void> updatePostWaySubOrder(PostWayHealthySubOrderReq req) {
        req.setMerchantId(getCurrentAdminUserId());
        return CommonResult.success(merchantHealthyService.updatePostWaySubOrder(req));
    }

    @RequestMapping(value = "/startDelivery", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = {"fxs", "ywy", "cxy"}, mode = SaMode.OR)
    public CommonResult<Void> startDelivery(@NotNull(message = "订单ID不能为空") Integer subOrderId) {
        return CommonResult.success(merchantHealthyService.startDelivery(subOrderId, getCurrentAdminUserId()));
    }

    @RequestMapping(value = "/completeDelivery", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = {"fxs", "ywy", "cxy"}, mode = SaMode.OR)
    public CommonResult<Void> completeDelivery(@NotNull(message = "订单ID不能为空") Integer subOrderId) {
        return CommonResult.success(merchantHealthyService.completeDelivery(subOrderId, getCurrentAdminUserId()));
    }
}
