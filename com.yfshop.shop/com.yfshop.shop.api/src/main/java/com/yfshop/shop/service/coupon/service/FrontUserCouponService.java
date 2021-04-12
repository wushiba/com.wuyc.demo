package com.yfshop.shop.service.coupon.service;

import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.activity.result.YfDrawPrizeResult;
import com.yfshop.shop.service.coupon.request.QueryUserCouponReq;
import com.yfshop.shop.service.coupon.result.YfCouponResult;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;
import java.util.List;

/**
 * @Title:用户优惠券Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-23 16:24:25
 * @Version:1.1.0
 */
public interface FrontUserCouponService {

	/**
	 * 根据id查询优惠券信息
	 * @param couponId	优惠券id
	 * @return	YfCouponResult
	 * @throws ApiException
	 */
	public YfCouponResult getCouponResultById(Integer couponId) throws ApiException;

	/**
	 * 查询用户优惠券YfUserCoupon
	 * @param userCouponReq	查询条件
	 * @return	List<YfUserCouponResult>
	 */
	public List<YfUserCouponResult> findUserCouponList(QueryUserCouponReq userCouponReq) throws ApiException;

	public List<YfUserCouponResult> findAllUserDrawRecordList(QueryUserCouponReq userCouponReq) throws ApiException;

	/**
	 * 用户使用优惠券，修改券状态
	 * @param userCouponId
	 * @throws ApiException
	 */
	public void useUserCoupon(Long userCouponId) throws ApiException;

	/**
	 * 修改优惠券对应的订单
	 * @param userCouponId	用户优惠券id
	 * @param childOrderId	子订单id
	 * @throws ApiException
	 */
	public void updateCouponOrderOrderId(Long userCouponId, Long childOrderId) throws ApiException;

	/**
	 * 用户抽中优惠券后生成优惠券
	 * @param userId				用户id
	 * @param actCode				用户扫码抽奖的码，yf_act_code_batch_detail表的actCode
	 * @param drawPrizeResult		奖品信息
	 * @return
	 * @throws ApiException
	 */
	public YfUserCouponResult createUserCouponByPrize(Integer userId, String actCode, YfDrawPrizeResult drawPrizeResult) throws ApiException;

}
