package com.yfshop.shop.service.impl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.yfshop.code.mapper.CouponMapper;
import com.yfshop.code.mapper.UserCouponMapper;
import com.yfshop.code.mapper.UserMapper;
import com.yfshop.code.model.Coupon;
import com.yfshop.code.model.User;
import com.yfshop.code.model.UserCoupon;
import com.yfshop.common.enums.CouponResourceEnum;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.request.QueryCouponReq;
import com.yfshop.shop.request.QueryUserCouponReq;
import com.yfshop.shop.result.YfCouponResult;
import com.yfshop.shop.result.YfUserCouponResult;
import com.yfshop.shop.service.ActivityCouponService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

/**
 * @Title:平台优惠券Service实现
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-23 13:47:17
 * @Version:1.1.0
 */
@Service
public class ActivityCouponServiceImpl implements ActivityCouponService {

	@Resource
	private UserMapper userMapper;

	@Resource
	private CouponMapper couponMapper;

	@Resource
	private UserCouponMapper userCouponMapper;

	@Override
	public List<YfCouponResult> getAll(QueryCouponReq req) throws ApiException {
		Coupon coupon = BeanUtil.convert(req, Coupon.class);
		LambdaQueryWrapper<Coupon> queryWrapper = Wrappers.<Coupon>lambdaQuery().setEntity(coupon);
		List<Coupon> dataList = couponMapper.selectList(queryWrapper);
		return BeanUtil.convertList(dataList, YfCouponResult.class);
	}

	@Override
	public List<YfUserCouponResult> findUserCouponList(QueryUserCouponReq req) throws ApiException {
		LambdaQueryWrapper<UserCoupon> queryWrapper = Wrappers.lambdaQuery(UserCoupon.class)
				.eq(req.getCouponId() != null, UserCoupon :: getCouponId, req.getCouponId())
				.eq(req.getUseStatus() != null, UserCoupon :: getUseStatus, req.getUseStatus())
				.eq(req.getOrderId() != null, UserCoupon :: getOrderId, req.getOrderId())
				.eq(StringUtils.isNotBlank(req.getCouponResource()), UserCoupon :: getCouponResource, req.getCouponResource())
				.orderByDesc(UserCoupon :: getId);

		List<UserCoupon> dataList = userCouponMapper.selectList(queryWrapper);
		return BeanUtil.convertList(dataList, YfUserCouponResult.class);
	}

	@Override
	public YfUserCouponResult createUserCoupon(Integer userId, Integer couponId) throws ApiException {
		User user = userMapper.selectById(userId);
		Asserts.assertNonNull(user, 500, "用户不存在,请先授权关注公众号");

		Coupon coupon = couponMapper.selectById(couponId);
		Asserts.assertNonNull(coupon, 500, "优惠券不存在");

		String validType = coupon.getValidType();
		LocalDateTime startDate = null, endDate = null;
		LocalDateTime now = LocalDateTime.now();
		if ("DATE_RANGE".equalsIgnoreCase(validType)) {
			startDate = coupon.getValidStartTime();
			endDate = coupon.getValidEndTime();
		} else if ("TODAY".equalsIgnoreCase(validType)) {
			startDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
			endDate = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
		} else if ("FIX_DAY".equalsIgnoreCase(validType)) {
			startDate = now;
			endDate = now.plusDays(coupon.getValidDay());
		}

		UserCoupon userCoupon = new UserCoupon();
		userCoupon.setCreateTime(now);
		userCoupon.setUserId(userId);
		userCoupon.setMerchantId(null);
		userCoupon.setPidPath(null);
		userCoupon.setCouponId(couponId);
		userCoupon.setCouponTitle(coupon.getCouponTitle());
		userCoupon.setValidStartTime(startDate);
		userCoupon.setValidEndTime(endDate);
		userCoupon.setCouponPrice(coupon.getCouponPrice());
		userCoupon.setUseConditionPrice(coupon.getUseConditionPrice());
		userCoupon.setCouponResource(CouponResourceEnum.DRAW.getCode());
		userCoupon.setUseRangeType(coupon.getUseRangeType());
		userCoupon.setCanUseItemIds(coupon.getCanUseItemIds());
		userCoupon.setCouponDesc(coupon.getCouponDesc());

		// TODO: 2021/3/23 手机号用户还没有？
		userCoupon.setMobile(null);
		userCoupon.setNickname(user.getNickname());
		userCoupon.setUseStatus("N");
		userCoupon.setUseTime(now);
		userCoupon.setOrderId(null);
		userCouponMapper.insert(userCoupon);
		return BeanUtil.convert(userCoupon, YfUserCouponResult.class);
	}

}

