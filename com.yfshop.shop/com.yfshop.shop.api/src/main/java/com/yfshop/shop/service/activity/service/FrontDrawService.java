package com.yfshop.shop.service.activity.service;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.yfshop.shop.service.activity.result.YfActCodeBatchDetailResult;
import com.yfshop.shop.service.activity.result.YfDrawActivityResult;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;

/**
 * @Title:活动抽奖Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 19:09:17
 * @Version:1.1.0
 */
public interface FrontDrawService {

	/**
	 * 查询抽奖活动
	 * @param id	抽奖活动id
	 * @return
	 * @throws ApiException
	 */
	public YfDrawActivityResult getDrawActivityById(Integer id) throws ApiException;

	/**
	 * 查询抽奖活动和奖品详情
	 * @param id	抽奖活动id
	 * @return
	 * @throws ApiException
	 */
	public YfDrawActivityResult getDrawActivityDetailById(Integer id) throws ApiException;

	/**
	 * 用户抽奖
	 * @param userId	用户id
	 * @param ipStr		用户当前所在id
	 * @param actCode	活动码
	 * @return
	 * @throws ApiException
	 */
	public YfUserCouponResult userClickDraw(Integer userId, String ipStr, String actCode) throws ApiException;

	/**
	 * 根据用户id获取用户信息
	 * @param actCode
	 * @return
	 * @Description:
	 */
	public YfActCodeBatchDetailResult getYfActCodeBatchDetailByActCode(String actCode) throws ApiException;

}
