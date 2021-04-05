package com.yfshop.shop.service.coupon;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.CouponMapper;
import com.yfshop.code.mapper.UserCouponMapper;
import com.yfshop.code.model.Coupon;
import com.yfshop.code.model.UserCoupon;
import com.yfshop.code.model.WebsiteCodeDetail;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.coupon.result.YfCouponResult;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;
import com.yfshop.shop.service.coupon.service.FrontUserCouponService;
import com.yfshop.shop.service.merchant.FrontMerchantServiceImpl;
import com.yfshop.shop.service.merchant.result.WebsiteCodeDetailResult;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

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
     * @param userId	用户id
     * @param isCanUse	是否可用， 可用传Y， 不可用传N
     * @param couponId	优惠券id， 没有可以不传
     * @return
     * @throws ApiException
     */
    @Override
    public List<YfUserCouponResult> findUserCanUseCouponList(Integer userId, String isCanUse, Integer couponId) throws ApiException {
        LambdaQueryWrapper<UserCoupon> queryWrapper = Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getUserId, userId);

        if ("Y".equalsIgnoreCase(isCanUse)) {
            queryWrapper.eq(UserCoupon::getUseStatus, "Y")
                    .gt(UserCoupon::getValidEndTime, new Date());
        } else {
            queryWrapper.eq(UserCoupon::getUseStatus, "N")
                    .lt(UserCoupon::getValidEndTime, new Date());
        }

        if (couponId != null) {
            queryWrapper.eq(UserCoupon::getCouponId, couponId);
        }

        queryWrapper.orderByDesc(UserCoupon::getId);
        List<UserCoupon> dataList = userCouponMapper.selectList(queryWrapper);
        return BeanUtil.convertList(dataList, YfUserCouponResult.class);
    }

}

