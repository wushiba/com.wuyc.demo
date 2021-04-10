package com.yfshop.shop.service.activity;

import cn.hutool.core.net.NetUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.BoxSpecValEnum;
import com.yfshop.common.enums.ProvinceEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.activity.result.YfActCodeBatchDetailResult;
import com.yfshop.shop.service.activity.result.YfDrawActivityResult;
import com.yfshop.shop.service.activity.result.YfDrawPrizeResult;
import com.yfshop.shop.service.activity.service.FrontDrawService;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;
import com.yfshop.shop.service.coupon.service.FrontUserCouponService;
import com.yfshop.shop.service.user.result.UserResult;
import com.yfshop.shop.service.user.service.FrontUserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @Title:活动抽奖Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 19:09:17
 * @Version:1.1.0
 */
@DubboService
@EnableAsync
public class FrontDrawServiceImpl implements FrontDrawService {

    private static final Logger logger = LoggerFactory.getLogger(FrontDrawServiceImpl.class);

    @Resource
    private RedisService redisService;
    @Resource
    private DrawPrizeMapper drawPrizeMapper;
    @Resource
    private IpAddressMapper ipAddressMapper;
    @Resource
    private UserCouponMapper userCouponMapper;
    @Resource
    private FrontUserService frontUserService;
    @Resource
    private DrawActivityMapper drawActivityMapper;
    @Resource
    private FrontUserCouponService frontUserCouponService;
    @Resource
    private DrawProvinceRateMapper drawProvinceRateMapper;
    @Resource
    private ActCodeBatchDetailMapper actCodeBatchDetailMapper;

    @Override
    public YfDrawActivityResult getDrawActivityById(Integer id) throws ApiException {
        DrawActivity drawActivity = null;
        Object activityObject = redisService.get(CacheConstants.DRAW_ACTIVITY_PREFIX + id);
        if (activityObject != null) {
            drawActivity = JSON.parseObject(activityObject.toString(), DrawActivity.class);
        } else {
            drawActivity = drawActivityMapper.selectById(id);
        }
        if (drawActivity == null) {
            return null;
        }
        return BeanUtil.convert(drawActivity, YfDrawActivityResult.class);
    }

    @Override
    public YfDrawActivityResult getDrawActivityDetailById(Integer id) throws ApiException {
        DrawActivity drawActivity = null;
        Object activityObject = redisService.get(CacheConstants.DRAW_ACTIVITY_PREFIX + id);
        if (activityObject != null) {
            drawActivity = JSON.parseObject(activityObject.toString(), DrawActivity.class);
        } else {
            drawActivity = drawActivityMapper.selectById(id);
        }
        if (drawActivity == null) {
            return null;
        }

        List<DrawPrize> prizeList = null;
        Object prizeObject = redisService.get(CacheConstants.DRAW_PRIZE_NAME_PREFIX + id);
        if (prizeObject != null) {
            prizeList = JSON.parseArray(prizeObject.toString(), DrawPrize.class);
        } else {
            prizeList = drawPrizeMapper.selectList(Wrappers.lambdaQuery(DrawPrize.class)
                    .eq(DrawPrize::getActId, id));
            redisService.set(CacheConstants.DRAW_PRIZE_NAME_PREFIX + id,
                    JSON.toJSONString(prizeList), 60 * 60 * 24 * 30);
        }

        YfDrawActivityResult activityResult = BeanUtil.convert(drawActivity, YfDrawActivityResult.class);
        activityResult.setPrizeList(BeanUtil.convertList(prizeList, YfDrawPrizeResult.class));
        return activityResult;
    }

    /**
     * 用户点击抽奖
     * @param userId  用户id
     * @param ipStr   用户当前所在id
     * @param actCode 活动码
     * @throws ApiException
     */
    @Override
    public YfUserCouponResult userClickDraw(Integer userId, String ipStr, String actCode) throws ApiException {
        UserResult user = frontUserService.getUserById(userId);
        Asserts.assertNonNull(user, 500, "用户不存在,请先授权关注公众号");

        YfActCodeBatchDetailResult actCodeBatchDetail = this.getYfActCodeBatchDetailByActCode(actCode);
        Asserts.assertNonNull(actCodeBatchDetail, 500, "请扫描正确的券码");
        Integer drawActivityId = actCodeBatchDetail.getActId();

        // 判断是否使用, 根据actCode查询用户优惠券表
        if ("pro".equalsIgnoreCase(SpringUtil.getActiveProfile()) || !"2bfdd1cc48ac96a9".equalsIgnoreCase(actCode)) {
            UserCoupon userCoupon = userCouponMapper.selectOne(Wrappers.lambdaQuery(UserCoupon.class).eq(UserCoupon::getActCode, actCode));
            Asserts.assertNull(userCoupon, 503, "请勿重复扫码抽奖");
        }

        // 获取奖品，每个奖品登记优惠券id， 可以走缓存
        YfDrawActivityResult yfDrawActivityResult = getDrawActivityDetailById(drawActivityId);
        Asserts.assertNonNull(yfDrawActivityResult, 501, "活动不存在,请联系管理员");
        Asserts.assertEquals(yfDrawActivityResult.getIsEnable(), "Y", 500, "活动暂未开启,请联系管理员");
        Asserts.assertFalse(yfDrawActivityResult.getStartTime().isAfter(LocalDateTime.now()), 501, "活动暂未开始,请稍后再试");
        Asserts.assertFalse(yfDrawActivityResult.getEndTime().isBefore(LocalDateTime.now()), 501, "活动暂已结束,请稍后再试");
        List<YfDrawPrizeResult> prizeList = yfDrawActivityResult.getPrizeList();
        Asserts.assertCollectionNotEmpty(prizeList, 500, "活动暂未配置奖品，请稍微再试");
        Map<Integer, List<YfDrawPrizeResult>> prizeMap = prizeList.stream().collect(Collectors
                .groupingBy(YfDrawPrizeResult::getPrizeLevel));
        Integer prizeLevel = 3;
        YfDrawPrizeResult firstPrize = prizeMap.get(1).get(0);
        YfDrawPrizeResult secondPrize = prizeMap.get(2).get(0);
        YfDrawPrizeResult thirdPrize = prizeMap.get(3).get(0);
        Integer couponId = thirdPrize.getCouponId();

        // 根据ip查询地址, 找不到归属地默认抽到三等奖
        Integer provinceId = this.getProvinceByIpStr(ipStr);

        YfUserCouponResult result = new YfUserCouponResult();
        result.setCouponTitle(thirdPrize.getPrizeTitle());
        result.setDrawPrizeIcon(thirdPrize.getPrizeIcon());
        if (provinceId == null) {
            logger.info("======抽奖用户userId=" + userId +  ",actCode=" + actCode + ",抽奖结果=" + JSON.toJSONString(result));
            frontUserCouponService.createUserCoupon(userId, drawActivityId, prizeLevel, couponId, actCode);
            return result;
        }

        // 判断省份抽奖规则有没有走定制化, 找不到根据活动奖品概率去发奖品, 根据大盒小盒,去抽奖
        DrawProvinceRate provinceRate = this.getProvinceRateByActIdAndProvince(actCodeBatchDetail.getActId(), provinceId);
        if (provinceRate == null) {
            if (BoxSpecValEnum.BIG.getCode().equalsIgnoreCase(actCodeBatchDetail.getBoxSpecVal())) {
                prizeLevel = startDraw(firstPrize.getWinRate(), secondPrize.getWinRate());
                couponId = prizeMap.get(prizeLevel).get(0).getCouponId();
                frontUserCouponService.createUserCoupon(userId, drawActivityId, prizeLevel, couponId, actCode);
            } else {
                prizeLevel = startDraw(firstPrize.getWinRate(), secondPrize.getSmallBoxRate());
                couponId = prizeMap.get(prizeLevel).get(0).getCouponId();
                frontUserCouponService.createUserCoupon(userId, drawActivityId, prizeLevel, couponId, actCode);
            }
        } else {
            if (BoxSpecValEnum.BIG.getCode().equalsIgnoreCase(actCodeBatchDetail.getBoxSpecVal())) {
                prizeLevel = startDraw(provinceRate.getFirstWinRate(), provinceRate.getSecondWinRate());
                couponId = prizeMap.get(prizeLevel).get(0).getCouponId();
                frontUserCouponService.createUserCoupon(userId, drawActivityId, prizeLevel, couponId, actCode);
            } else {
                prizeLevel =startDraw(provinceRate.getFirstWinRate(), provinceRate.getSecondSmallBoxWinRate());
                couponId = prizeMap.get(prizeLevel).get(0).getCouponId();
                frontUserCouponService.createUserCoupon(userId, drawActivityId, prizeLevel, couponId, actCode);
            }
        }

        result.setCouponTitle(prizeMap.get(prizeLevel).get(0).getPrizeTitle());
        result.setDrawPrizeIcon(prizeMap.get(prizeLevel).get(0).getPrizeIcon());
        logger.info("======抽奖用户userId=" + userId +  ",actCode=" + actCode + ",抽奖结果=" + JSON.toJSONString(result));
        return result;
    }

    @Override
    public YfActCodeBatchDetailResult getYfActCodeBatchDetailByActCode(String actCode) throws ApiException {
        Asserts.assertStringNotBlank(actCode, 500, "请扫描正确的券码");

        ActCodeBatchDetail actCodeBatchDetail = null;
        Object userObject = redisService.get(CacheConstants.ACT_CODE_BATCH_ACT_NO + actCode);
        if (userObject != null) {
            actCodeBatchDetail = JSON.parseObject(userObject.toString(), ActCodeBatchDetail.class);
        } else {
            actCodeBatchDetail = actCodeBatchDetailMapper.selectOne(Wrappers
                    .lambdaQuery(ActCodeBatchDetail.class).eq(ActCodeBatchDetail::getActCode, actCode));
            redisService.set(CacheConstants.ACT_CODE_BATCH_ACT_NO + actCode,
                    JSON.toJSONString(actCodeBatchDetail), 60 * 30);
        }
        return actCodeBatchDetail == null ? null : BeanUtil.convert(actCodeBatchDetail, YfActCodeBatchDetailResult.class) ;
    }


    private DrawProvinceRate getProvinceRateByActIdAndProvince(Integer actId, Integer provinceId) {
        List<DrawProvinceRate> provinceList = null;
        Object provinceObject = redisService.get(CacheConstants.DRAW_PROVINCE_RATE_PREFIX + actId);
        if (provinceObject != null) {
            provinceList = JSON.parseArray(provinceObject.toString(), DrawProvinceRate.class);
        } else {
            provinceList = drawProvinceRateMapper.selectList(Wrappers.lambdaQuery(DrawProvinceRate.class)
                    .eq(DrawProvinceRate::getActId, actId));
            redisService.set(CacheConstants.DRAW_PROVINCE_RATE_PREFIX + actId,
                    JSON.toJSONString(provinceList), 60 * 60 * 24 * 30);
        }
        if (CollectionUtils.isEmpty(provinceList)) {
            return null;
        }
        List<DrawProvinceRate> dataList = provinceList.stream().filter(drawProvinceRate ->
                drawProvinceRate.getProvinceId().intValue() == provinceId)
                .collect(Collectors.toList());
        return dataList.size() > 0 ? dataList.get(0) : null;
    }

    /**
     * 根据用户ip地址 获取所在省份
     * @param ipStr
     * @return  provinceId || null
     */
    private Integer getProvinceByIpStr(String ipStr) {
        if (!"pro".equalsIgnoreCase(SpringUtil.getActiveProfile())) {
            return ProvinceEnum.SHANDONG.getId();
        }

        IpAddress ipAddress = null;
        long ipLong = NetUtil.ipv4ToLong(ipStr);
        Object ipStrObject = redisService.get(CacheConstants.USER_REQUEST_IP_STR + ipLong);
        if (ipStrObject != null) {
            ipAddress = JSON.parseObject(ipStrObject.toString(), IpAddress.class);
        } else {
            ipAddress = ipAddressMapper.selectOne(Wrappers.lambdaQuery(IpAddress.class)
                    .le(IpAddress::getIpStartLong, ipLong).ge(IpAddress::getIpEndLong, ipLong)
                    .orderByDesc(IpAddress::getId));
            redisService.set(CacheConstants.USER_REQUEST_IP_STR + ipLong,
                    JSON.toJSONString(ipAddress), 60 * 30);
        }

        // 找不到地址默认为三等奖
        if (ipAddress == null) {
            return null;
        }

        // 找不到归属地默认抽到三等奖
        String provincePrefix = ipAddress.getAddress().substring(0, 2);
        ProvinceEnum provinceEnum = ProvinceEnum.getByPrefix(provincePrefix);
        if (provinceEnum == null) {
            return null;
        }
        return provinceEnum.getId();
    }


    /**
     * 生成随机字符串抽奖
     * @param firstRate
     * @param secondRate
     * @return
     */
    private Integer startDraw(Integer firstRate, Integer secondRate) {
        Integer prizeLevel = 3;
        int random = new Random().nextInt(10000);
        if (random <= firstRate) {
            // 校验是否超卖, 超卖的话默认抽中三等奖
            prizeLevel = 1;
        } else if (random <= (firstRate + secondRate)) {
            prizeLevel = 2;
        }
        logger.info("抽中了" + prizeLevel + "等奖");
        return prizeLevel;
    }

}

