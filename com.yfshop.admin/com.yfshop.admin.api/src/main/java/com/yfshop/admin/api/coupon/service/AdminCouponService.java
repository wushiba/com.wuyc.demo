package com.yfshop.admin.api.coupon.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.coupon.request.CreateCouponReq;
import com.yfshop.admin.api.coupon.request.QueryCouponReq;
import com.yfshop.admin.api.coupon.result.CouponRulesResult;
import com.yfshop.admin.api.coupon.result.YfCouponResult;
import com.yfshop.common.exception.ApiException;

import java.util.List;

/**
 * @Title:平台优惠券Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-23 13:47:17
 * @Version:1.1.0
 */
public interface AdminCouponService {

	/**
	 *
	 *
	 * 通过id得到平台优惠券YfCoupon
	 * @param id
	 * @return 
	 * @Description:
	 */
	public YfCouponResult getYfCouponById(Integer id) throws ApiException;

	/**
	 * 分页查询平台优惠券YfCoupon
	 * @param req
	 * @return 
	 * @Description:
	 */
	public IPage<YfCouponResult> findYfCouponListByPage(QueryCouponReq req) throws ApiException;

	/**
	 * 得到所有平台优惠券YfCoupon
	 * @param req
	 * @return 
	 * @Description:
	 */
	public List<YfCouponResult> getAll(QueryCouponReq req) throws ApiException;

	/**
	 * 添加平台优惠券YfCoupon
	 * @param couponReq
	 * @Description:
	 */
	public void insertYfCoupon(CreateCouponReq couponReq) throws ApiException;
	
	/**
	 * 通过id修改平台优惠券YfCoupon throws ApiException;
	 * @param couponReq
	 * @Description:
	 */
	public void updateYfCoupon(CreateCouponReq couponReq) throws ApiException;

	/**
	 * 删除平台优惠券
	 * @param couponId
	 * @Description:
	 */
	public void deleteYfCoupon(Integer couponId) throws ApiException;

	/**
	 * 查询平台优惠券
	 * @param couponId
	 * @Description:
	 */
	YfCouponResult findYfCoupon(Integer couponId) throws ApiException;

	/**
	 * 上下架平台优惠券
	 * @param couponId
	 * @Description:
	 */
	public void updateCouponStatus(Integer couponId, String isEnable) throws ApiException;


	public List<CouponRulesResult> getCouponRulesList();
	
}
