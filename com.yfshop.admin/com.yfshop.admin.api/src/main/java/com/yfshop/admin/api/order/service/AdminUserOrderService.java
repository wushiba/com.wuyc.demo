package com.yfshop.admin.api.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.yfshop.admin.api.order.request.OrderExpressReq;
import com.yfshop.admin.api.order.request.QueryOrderReq;
import com.yfshop.admin.api.order.result.OrderDetailResult;
import com.yfshop.admin.api.order.result.OrderResult;
import com.yfshop.common.exception.ApiException;

import java.util.List;

/**
 * @Title:用户订单Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-31 16:09:37
 * @Version:1.1.0
 */
public interface AdminUserOrderService {

    /**
     * 用户付款后修改订单状态
     *
     * @param orderId 订单id
     * @param billNo  支付流水号
     * @return
     * @throws ApiException
     */
    Void updateOrderPayStatus(Long orderId, String billNo) throws ApiException;

    /**
     * 用户确认订单
     *
     * @param userId        用户id
     * @param orderDetailId 订单详情id
     * @throws ApiException
     */
    Void confirmOrder(Integer userId, Long orderDetailId) throws ApiException;

    /**
     * 查询订列表
     *
     * @param req
     * @return
     */
    IPage<OrderResult> list(QueryOrderReq req) throws ApiException;

    /**
     * 关闭订单
     *
     * @param id
     * @return
     * @throws ApiException
     */
    Void closeOrder(Long id) throws ApiException, WxPayException;

    /**
     * 关闭订单
     *
     * @param orderExpressReq
     * @return
     * @throws ApiException
     */
    Void updateOrderExpress(OrderExpressReq orderExpressReq) throws ApiException;

    /**
     * 查看订单详情
     *
     * @param id
     * @return
     */
    OrderDetailResult getOrderDetail(Long id) throws ApiException;

    Void trySendStoOrder(Long id);
}
