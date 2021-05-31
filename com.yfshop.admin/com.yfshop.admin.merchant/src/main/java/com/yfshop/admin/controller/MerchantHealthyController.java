package com.yfshop.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.healthy.MerchantHealthyService;
import com.yfshop.admin.api.healthy.request.PostWayHealthySubOrderReq;
import com.yfshop.admin.api.healthy.request.QueryJxsHealthySubOrderReq;
import com.yfshop.admin.api.healthy.request.QueryMerchantHealthySubOrdersReq;
import com.yfshop.admin.api.healthy.result.HealthySubOrderResult;
import com.yfshop.admin.api.merchant.request.QueryMerchantReq;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.common.api.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping(value = "/saveMerchant", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<IPage<HealthySubOrderResult>> pageQueryMerchantHealthySubOrders() {
        QueryMerchantHealthySubOrdersReq req = new QueryMerchantHealthySubOrdersReq();
        req.setPageIndex(1);
        req.setPageSize(0);
        req.setMerchantId(0);
        req.setOrderStatus("");
        return CommonResult.success(merchantHealthyService.pageQueryMerchantHealthySubOrders(req));
    }

    @RequestMapping(value = "/getJxsSubOrderList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<IPage<HealthySubOrderResult>> pageJxsSubOrderList(QueryJxsHealthySubOrderReq req) {
        req.setMerchantId(getCurrentUserId());
        return CommonResult.success(merchantHealthyService.pageJxsSubOrderList(req));
    }


    @RequestMapping(value = "/getMerchantHealthyList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<IPage<MerchantResult>> pageMerchantHealthyList(QueryMerchantReq req) {
        req.setMerchantId(getCurrentUserId());
        return CommonResult.success(merchantHealthyService.pageMerchantHealthyList(getRequestIpStr(),req));
    }


    @RequestMapping(value = "/updatePostWaySubOrder", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<Void> updatePostWaySubOrder(PostWayHealthySubOrderReq req) {
        req.setMerchantId(getCurrentUserId());
        return CommonResult.success(merchantHealthyService.updatePostWaySubOrder(req));
    }
}
