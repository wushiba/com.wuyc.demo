package com.yfshop.shop.service.healthy;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.healthy.req.SubmitHealthyOrderReq;
import com.yfshop.shop.service.healthy.result.HealthyItemResult;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    List<Object> queryHealthyActivities();
}
