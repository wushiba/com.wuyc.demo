package com.yfshop.shop.service.coupon;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.CouponMapper;
import com.yfshop.code.mapper.UserCouponMapper;
import com.yfshop.code.model.Coupon;
import com.yfshop.code.model.UserCoupon;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.UserCouponStatusEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.dao.UserCouponDao;
import com.yfshop.shop.service.coupon.request.QueryUserCouponReq;
import com.yfshop.shop.service.coupon.result.YfCouponResult;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;
import com.yfshop.shop.service.coupon.service.FrontUserCouponService;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title:用户优惠券Service实现
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-23 16:24:25
 * @Version:1.1.0
 */
@Service(dynamic = true)
public class FrontUserCouponServiceImpl implements FrontUserCouponService {

    private static final Logger logger = LoggerFactory.getLogger(FrontUserCouponServiceImpl.class);

    @Resource
    private RedisService redisService;

    @Resource
    private CouponMapper couponMapper;

    @Resource
    private UserCouponMapper userCouponMapper;

    @Resource
    private UserCouponDao userCouponDao;


    /**
     * 根据id查询优惠券信息
     * @param couponId	优惠券id
     * @return YfCouponResult
     * @throws ApiException
     */
    @Override
    public YfCouponResult getCouponResultById(Integer couponId) throws ApiException {
        Asserts.assertNonNull(couponId, 500, "优惠券id不可以为空");
        Object couponObject = redisService.get(CacheConstants.COUPON_INFO_DATA);
        if (couponObject != null) {
            return JSON.parseObject(couponObject.toString(), YfCouponResult.class);
        }
        Coupon coupon = couponMapper.selectOne(Wrappers.lambdaQuery(Coupon.class).eq(Coupon::getId, couponId));

        Asserts.assertNonNull(coupon, 500, "优惠券不存在");

        YfCouponResult yfCouponResult = BeanUtil.convert(coupon, YfCouponResult.class);
        redisService.set(CacheConstants.MERCHANT_WEBSITE_CODE, JSON.toJSONString(yfCouponResult), 60 * 60 * 24);
        return yfCouponResult;
    }

    /**
     * 查询用户优惠券YfUserCoupon
     * @param userCouponReq	查询条件
     * @return
     * @throws ApiException
     */
    @Override
    public List<YfUserCouponResult> findUserCouponList(QueryUserCouponReq userCouponReq) throws ApiException {
        Asserts.assertNonNull(userCouponReq.getUserId(), 500, "用户id不可以为空");

        LambdaQueryWrapper<UserCoupon> queryWrapper = Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getUserId, userCouponReq.getUserId());

        if ("Y".equalsIgnoreCase(userCouponReq.getIsCanUse())) {
            queryWrapper.eq(UserCoupon::getUseStatus, UserCouponStatusEnum.NO_USE.getCode())
                    .gt(UserCoupon::getValidEndTime, new Date());
        } else {
            queryWrapper.in(UserCoupon::getUseStatus, UserCouponStatusEnum.IN_USE.getCode()
                    , UserCouponStatusEnum.HAS_USE.getCode())
                    .lt(UserCoupon::getValidEndTime, new Date());
        }

        if (userCouponReq.getCouponId() != null) {
            queryWrapper.eq(UserCoupon::getCouponId, userCouponReq.getCouponId());
        }

        if (userCouponReq.getDrawPrizeLevel() != null) {
            queryWrapper.eq(UserCoupon::getDrawPrizeLevel, userCouponReq.getDrawPrizeLevel());
        }

        queryWrapper.orderByDesc(UserCoupon::getId);
        List<UserCoupon> dataList = userCouponMapper.selectList(queryWrapper);
        List<YfUserCouponResult> resultList = BeanUtil.convertList(dataList, YfUserCouponResult.class);

        if (userCouponReq.getItemId() == null) {
            return resultList;
        }
        return resultList.stream().filter(data -> "ALL".equalsIgnoreCase(data.getUseRangeType()) ||
                data.getCanUseItemIds().contains(userCouponReq.getItemId() + "")).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void useUserCoupon(Long userCouponId) throws ApiException {
        Asserts.assertNonNull(userCouponId, 500, "用户优惠券id不可以为空");
        int result = userCouponDao.updateUserCouponInUse(userCouponId);
        Asserts.assertFalse(result < 1, 500, "优惠券使用失败，不可以重复使用");
    }

}

