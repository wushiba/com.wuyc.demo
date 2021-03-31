package com.yfshop.shop.service.order.service;

import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.order.result.OrderResult;
import java.util.List;

/**
 * @Title:用户订单Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-31 16:09:37
 * @Version:1.1.0
 */
public interface FrontUserOrderService {

	/**
	 * 用户订单列表
	 * @param userId		用户id
	 * @param useStatus		订单状态
	 * @return
	 * @Description:
	 */
	public List<OrderResult> findUserOrderList(Integer userId, String useStatus) throws ApiException;

	/**
	 * 用户订单详情
	 * @param userId		用户id
	 * @param orderId		订单id
	 * @return
	 * @Description:
	 */
	public List<OrderResult> getUserOrderDetail(Integer userId, Integer orderId) throws ApiException;

	/**
	 * 用户取消订单
	 * @param userId
	 * @param orderId
	 * @throws ApiException
	 */
	public void cancelOrder(Integer userId, Integer orderId) throws ApiException;

	/**
	 * 用户确认订单
	 * @param userId
	 * @param orderId
	 * @throws ApiException
	 */
	public void confirmOrder(Integer userId, Integer orderId) throws ApiException;

}
