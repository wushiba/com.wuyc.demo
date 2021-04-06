package com.yfshop.shop.service;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.yfshop.shop.request.QueryCouponReq;
import com.yfshop.shop.request.QueryUserCouponReq;
import com.yfshop.shop.result.YfCouponResult;
import com.yfshop.shop.result.YfUserCouponResult;

import java.util.List;

/**
 * @Title:平台优惠券Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-23 13:47:17
 * @Version:1.1.0
 */
public interface ActivityCouponService {

	/**
	 * 得到所有平台优惠券YfCoupon
	 * @param req
	 * @return 
	 * @Description:
	 */
	public List<YfCouponResult> getAll(QueryCouponReq req) throws ApiException;

	public List<YfUserCouponResult> findUserCouponList(QueryUserCouponReq req) throws ApiException;

	/**
	 * 用户抽中优惠券后生成优惠券
	 * @param userId		用户id
	 * @param prizeLevel	奖品等级
	 * @param couponId		优惠券id
	 * @return
	 * @throws ApiException
	 */
	public YfUserCouponResult createUserCoupon(Integer userId, Integer prizeLevel, Integer couponId) throws ApiException;

}
