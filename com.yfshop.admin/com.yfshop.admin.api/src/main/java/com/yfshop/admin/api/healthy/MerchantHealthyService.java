package com.yfshop.admin.api.healthy;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.healthy.request.QueryMerchantHealthySubOrdersReq;
import com.yfshop.admin.api.healthy.result.HealthySubOrderResult;
import com.yfshop.common.exception.ApiException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Xulg
 * Description: TODO 请加入类描述信息
 * Created in 2021-05-31 10:48
 */
public interface MerchantHealthyService {

    // 业务员、分销商，促销员 查询订单
    IPage<HealthySubOrderResult> pageQueryMerchantHealthySubOrders(@Valid @NotNull QueryMerchantHealthySubOrdersReq req) throws ApiException;

    Void startDelivery(@NotNull(message = "订单ID不能为空") Integer subOrderId,
                       @NotNull(message = "配送商户ID不能为空") Integer merchantId) throws ApiException;

    Void completeDelivery(@NotNull(message = "订单ID不能为空") Integer subOrderId,
                          @NotNull(message = "配送商户ID不能为空") Integer merchantId) throws ApiException;
}
