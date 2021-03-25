package com.yfshop.shop.service;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.yfshop.shop.request.QueryCouponReq;
import com.yfshop.shop.request.QueryUserCouponReq;
import com.yfshop.shop.result.YfCouponResult;
import com.yfshop.shop.result.YfUserCouponResult;
import java.util.List;

/**
 * @Title:活动抽奖Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 19:09:17
 * @Version:1.1.0
 */
public interface ActivityDrawService {

	/**
	 * 用户抽奖
	 * @param userId	用户id
	 * @param ipStr		用户当前所在id
	 * @param actCode	活动码
	 * @return
	 * @throws ApiException
	 */
	public void createUserCoupon(Integer userId, String ipStr, String actCode) throws ApiException;

}
