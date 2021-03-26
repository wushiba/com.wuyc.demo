package com.yfshop.shop.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.yfshop.code.mapper.UserMapper;
import com.yfshop.code.model.Coupon;
import com.yfshop.code.model.IpAddress;
import com.yfshop.code.model.User;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.request.QueryCouponReq;
import com.yfshop.shop.result.YfCouponResult;
import com.yfshop.shop.result.YfUserResult;
import com.yfshop.shop.service.ActivityUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Title:用户Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-26 17:10:17
 * @Version:1.1.0
 */
@Service
public class ActivityUserServiceImpl implements ActivityUserService {

	@Resource
	private UserMapper userMapper;

	@Resource
	private RedisService redisService;

	@Override
	public YfUserResult getUserById(Integer userId) throws ApiException {
		if (userId == null || userId <= 0) {
			return null;
		}

		User user = null;
		Object userObject = redisService.get(CacheConstants.USER_INFO_ID + userId);
		if (userObject != null) {
			user = JSON.parseObject(userObject.toString(), User.class);
		} else {
			user = userMapper.selectById(userId);
			redisService.set(CacheConstants.USER_INFO_ID + userId,
					JSON.toJSONString(user), 60 * 30);
		}
		return user == null ? null : BeanUtil.convert(user, YfUserResult.class) ;
	}
}

