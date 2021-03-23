package com.yfshop.admin.api.coupon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.coupon.request.QueryUserCouponReq;
import com.yfshop.admin.api.coupon.result.YfUserCouponResult;
import com.yfshop.common.exception.ApiException;
import java.util.List;

/**
 * @Title:用户优惠券Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-23 16:24:25
 * @Version:1.1.0
 */
public interface AdminUserCouponService {

	/**
	 * 通过id得到用户优惠券YfUserCoupon
	 * @param id
	 * @return
	 * @Description:
	 */
	public YfUserCouponResult getYfUserCouponById(Integer id) throws ApiException;

	/**
	 * 分页查询用户优惠券YfUserCoupon
	 * @param req
	 * @return
	 * @Description:
	 */
	public Page<YfUserCouponResult> findYfUserCouponListByPage(QueryUserCouponReq req) throws ApiException;

	/**
	 * 得到所有用户优惠券YfUserCoupon
	 * @param yfUserCouponResult
	 * @return
	 * @Description:
	 */
	public List<YfUserCouponResult> getAll(QueryUserCouponReq yfUserCouponResult) throws ApiException;

}
