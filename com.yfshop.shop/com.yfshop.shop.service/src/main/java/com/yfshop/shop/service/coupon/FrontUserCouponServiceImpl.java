package com.yfshop.shop.service.coupon;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.CouponMapper;
import com.yfshop.code.mapper.UserCouponMapper;
import com.yfshop.code.mapper.UserMapper;
import com.yfshop.code.model.Coupon;
import com.yfshop.code.model.UserCoupon;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.CouponResourceEnum;
import com.yfshop.common.enums.UserCouponStatusEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.dao.UserCouponDao;
import com.yfshop.shop.service.activity.result.YfDrawPrizeResult;
import com.yfshop.shop.service.coupon.request.QueryUserCouponReq;
import com.yfshop.shop.service.coupon.result.YfCouponResult;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;
import com.yfshop.shop.service.coupon.service.FrontUserCouponService;
import com.yfshop.shop.service.user.result.UserResult;
import com.yfshop.shop.service.user.service.FrontUserService;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private UserMapper userMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private CouponMapper couponMapper;
    @Resource
    private UserCouponDao userCouponDao;
    @Resource
    private UserCouponMapper userCouponMapper;
    @Resource
    private FrontUserService frontUserService;

    /**
     * 根据id查询优惠券信息
     * @param couponId	优惠券id
     * @return YfCouponResult
     * @throws ApiException
     */
    @Override
    public YfCouponResult getCouponResultById(Integer couponId) throws ApiException {
        Asserts.assertNonNull(couponId, 500, "优惠券id不可以为空");
        Object couponObject = redisService.get(CacheConstants.COUPON_INFO_DATA + couponId);
        if (couponObject != null) {
            return JSON.parseObject(couponObject.toString(), YfCouponResult.class);
        } else {
            Coupon coupon = couponMapper.selectOne(Wrappers.lambdaQuery(Coupon.class).eq(Coupon::getId, couponId));
            YfCouponResult yfCouponResult = BeanUtil.convert(coupon, YfCouponResult.class);
            redisService.set(CacheConstants.COUPON_INFO_DATA + couponId, JSON.toJSONString(yfCouponResult), 60 * 60 * 24);
            return yfCouponResult;
        }
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
        } else if ("N".equalsIgnoreCase(userCouponReq.getIsCanUse())) {
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
        if (StringUtils.isNotBlank(userCouponReq.getCouponResource())) {
            queryWrapper.eq(UserCoupon::getCouponResource, userCouponReq.getCouponResource());
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

    @Override
    public void updateCouponOrderOrderId(Long userCouponId, Long childOrderId) throws ApiException {
        Asserts.assertNonNull(userCouponId, 500, "用户优惠券id不可以为空");
        Asserts.assertNonNull(childOrderId, 500, "订单id不可以为空");
    }


    /**
     * 用户抽中优惠券后生成优惠券
     * @param userId				用户id
     * @param actCode				用户扫码抽奖的码，yf_act_code_batch_detail表的actCode
     * @param drawPrizeResult		奖品信息
     * @return
     * @throws ApiException
     */
    @Async
    @Override
    public YfUserCouponResult createUserCouponByPrize(Integer userId, String actCode, YfDrawPrizeResult drawPrizeResult) throws ApiException {
        logger.info("======开始创建优惠券用户userId=" + userId +  ",actCode=" + actCode + ",开始创建优惠券");
        UserResult userResult = frontUserService.getUserById(userId);
        Asserts.assertNonNull(userResult, 500, "用户不存在,请先授权关注公众号");

        YfCouponResult coupon = getCouponResultById(drawPrizeResult.getCouponId());
        Asserts.assertNonNull(coupon, 500, "优惠券不存在");

        String validType = coupon.getValidType();
        LocalDateTime startDate = null, endDate = null;
        LocalDateTime now = LocalDateTime.now();
        if ("DATE_RANGE".equalsIgnoreCase(validType)) {
            startDate = coupon.getValidStartTime();
            endDate = coupon.getValidEndTime();
        } else if ("TODAY".equalsIgnoreCase(validType)) {
            endDate = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            startDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        } else if ("FIX_DAY".equalsIgnoreCase(validType)) {
            startDate = now;
            endDate = now.plusDays(coupon.getValidDay());
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setCreateTime(now);
        userCoupon.setUserId(userId);
        userCoupon.setMerchantId(null);
        userCoupon.setPidPath(null);
        userCoupon.setCouponId(drawPrizeResult.getCouponId());
        userCoupon.setCouponTitle(coupon.getCouponTitle());
        userCoupon.setValidStartTime(startDate);
        userCoupon.setValidEndTime(endDate);
        userCoupon.setActCode(actCode);
        userCoupon.setDrawPrizeLevel(drawPrizeResult.getPrizeLevel());
        userCoupon.setDrawActivityId(drawPrizeResult.getActId());
        userCoupon.setDrawPrizeIcon(drawPrizeResult.getPrizeIcon());
        userCoupon.setCouponPrice(coupon.getCouponPrice());
        userCoupon.setUseConditionPrice(coupon.getUseConditionPrice());
        userCoupon.setCouponResource(CouponResourceEnum.DRAW.getCode());
        userCoupon.setUseRangeType(coupon.getUseRangeType());
        userCoupon.setCanUseItemIds(coupon.getCanUseItemIds());
        userCoupon.setCouponDesc(coupon.getCouponDesc());

        // TODO: 2021/3/23 手机号用户还没有？
        userCoupon.setUseTime(null);
        userCoupon.setOrderId(null);
        userCoupon.setMobile(userResult.getMobile());
        userCoupon.setNickname(userResult.getNickname());
        userCoupon.setUseStatus(UserCouponStatusEnum.NO_USE.getCode());
        userCouponMapper.insert(userCoupon);
        logger.info("======结束创建优惠券用户userId=" + userId +  ",actCode=" + actCode + ",userCoupon=" + JSON.toJSONString(userCoupon));
        return BeanUtil.convert(userCoupon, YfUserCouponResult.class);
    }

}

