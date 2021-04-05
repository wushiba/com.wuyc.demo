package com.yfshop.shop.service.coupon.service;

import com.yfshop.common.exception.ApiException;
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
	 * @param userId	用户id
	 * @param isCanUse	是否可用， 可用传Y， 不可用传N
	 * @param couponId	优惠券id， 没有可以不传
	 * @return	List<YfUserCouponResult>
	 */
	public List<YfUserCouponResult> findUserCanUseCouponList(Integer userId, String isCanUse, Integer couponId) throws ApiException;

}
