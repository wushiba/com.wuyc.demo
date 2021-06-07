package com.yfshop.shop.service.healthy;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.healthy.req.PreviewShowShipPlansReq;
import com.yfshop.shop.service.healthy.req.QueryHealthyOrdersReq;
import com.yfshop.shop.service.healthy.req.SubmitHealthyOrderReq;
import com.yfshop.shop.service.healthy.result.HealthyActResult;
import com.yfshop.shop.service.healthy.result.HealthyItemResult;
import com.yfshop.shop.service.healthy.result.HealthyOrderResult;
import com.yfshop.shop.service.healthy.result.HealthySubOrderResult;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author Xulg
 * Description: TODO 请加入类描述信息
 * Created in 2021-05-26 15:51
 */
public interface HealthyService {

    WxPayMpOrderResult submitOrder(@NotNull(message = "用户ID不能为空") Integer userId,
                                   @Valid @NotNull SubmitHealthyOrderReq req) throws ApiException;

    List<HealthyItemResult> queryHealthyItems();

    HealthyItemResult findHealthyItemDetail(Integer itemId);

    List<HealthyActResult> queryHealthyActivities();

    IPage<HealthyOrderResult> pageQueryUserHealthyOrders(@Valid @NotNull QueryHealthyOrdersReq req);

    List<HealthySubOrderResult> pageQueryHealthyOrderDetail(Integer userId, Long orderId);

    HealthyActResult queryHealthyActivityDetail(Integer id);

    List<Date> previewShowShipPlans(@Valid @NotNull PreviewShowShipPlansReq req) throws ApiException;
}
