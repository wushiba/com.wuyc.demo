package com.yfshop.shop.service.order.service;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.order.result.YfUserOrderDetailResult;
import com.yfshop.shop.service.order.result.YfUserOrderListResult;
import java.util.List;
import java.util.Map;

/**
 * @Title:用户订单Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-31 16:09:37
 * @Version:1.1.0
 */
public interface FrontUserOrderService {

	/**
	 * 校验提交订单的时候是否支持自提
	 * @param userId	用户id
	 * @param itemId	商品id
	 * @param skuId		商品skuId
	 * @return	支持自提，返回true， 否则返回false
	 */
	Boolean checkSubmitOrderIsCanZt(Integer userId, Integer itemId, Integer skuId);

	/**
	 * 用户订单列表
	 * @param userId		用户id
	 * @param useStatus		订单状态
	 * @return
	 * @Description:
	 */
	public List<YfUserOrderListResult> findUserOrderList(Integer userId, String useStatus) throws ApiException;

	/**
	 * 用户订单详情
	 * @param userId			用户id
	 * @param orderId			订单id
	 * @param orderDetailId		订单详情id
	 * @return
	 * @throws ApiException
	 */
	public YfUserOrderDetailResult getUserOrderDetail(Integer userId, Long orderId, Long orderDetailId) throws ApiException;

	/**
	 * 用户取消订单
	 * @param userId	用户id
	 * @param orderId	订单id
	 * @throws ApiException
	 */
	public Void cancelOrder(Integer userId, Long orderId) throws ApiException;

	/**
	 * 用户确认订单
	 * @param userId			用户id
	 * @param orderDetailId		订单详情id
	 * @throws ApiException
	 */
	public Void confirmOrder(Integer userId, Long orderDetailId) throws ApiException;

	/**
	 * 商品单个立即购买
	 * @param userId		用户id
	 * @param skuId			skuId
	 * @param num			购买数量
	 * @param userCouponId	用户优惠券id
	 * @param addressId		用户地址id
	 * @return
	 * @throws ApiException
	 */
	Map<String, Object> submitOrderBySkuId(Integer userId, Integer skuId, Integer num, Long userCouponId, Long addressId) throws ApiException;

	/**
	 * 商品购物车下单购买
	 * @param userId		用户id
	 * @param cartIds		购物车id
	 * @param userCouponId	用户优惠券id
	 * @param addressId		用户地址id
	 * @return
	 * @throws ApiException
	 */
	Map<String, Object> submitOrderByCart(Integer userId, String cartIds, Long userCouponId, Long addressId) throws ApiException;

	/**
	 * 优惠券购买商品
	 * @param userId		用户id
	 * @param userCouponIds	用户优惠券ids
	 * @param userMobile	用户手机号
	 * @param websiteCode	商户网点码
	 * @return
	 * @throws ApiException
	 */
	Map<String, Object> submitOrderByUserCouponIds(Integer userId, String userCouponIds, String userMobile, String websiteCode) throws ApiException;

	/**
	 * 用户付款后修改订单状态
	 * @param orderId
	 * @return
	 * @throws ApiException
	 */
	Void updateOrderPayStatus(Long orderId) throws ApiException;

	/**
	 * 根据订单号唤起微信支付
	 * @param orderId	用户订单id
	 * @return
	 * @throws ApiException
	 */
	WxPayMpOrderResult userOrderToPay(Long orderId) throws WxPayException, ApiException;

}
