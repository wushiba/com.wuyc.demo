package com.yfshop.shop.service.activity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.BoxSpecValEnum;
import com.yfshop.common.enums.ProvinceEnum;
import com.yfshop.common.enums.UserCouponStatusEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.activity.result.YfActCodeBatchDetailResult;
import com.yfshop.shop.service.activity.result.YfDrawActivityResult;
import com.yfshop.shop.service.activity.result.YfDrawPrizeResult;
import com.yfshop.shop.service.activity.service.FrontDrawRecordService;
import com.yfshop.shop.service.activity.service.FrontDrawService;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;
import com.yfshop.shop.service.coupon.service.FrontUserCouponService;
import com.yfshop.shop.service.user.result.UserResult;
import com.yfshop.shop.service.user.service.FrontUserService;
import com.yfshop.shop.utils.Ip2regionUtil;
import com.yfshop.shop.utils.ProxyUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Title:活动抽奖Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 19:09:17
 * @Version:1.1.0
 */
@DubboService
public class FrontDrawServiceImpl implements FrontDrawService {

    private static final Logger logger = LoggerFactory.getLogger(FrontDrawServiceImpl.class);

    @Resource
    private RedisService redisService;
    @Resource
    private DrawPrizeMapper drawPrizeMapper;
    @Resource
    private UserCouponMapper userCouponMapper;
    @Resource
    private FrontUserService frontUserService;
    @Resource
    private FrontDrawRecordService frontDrawRecordService;
    @Resource
    private DrawActivityMapper drawActivityMapper;
    @Resource
    private FrontUserCouponService frontUserCouponService;
    @Resource
    private DrawProvinceRateMapper drawProvinceRateMapper;
    @Resource
    private ActCodeBatchDetailMapper actCodeBatchDetailMapper;
    @Resource
    private TraceMapper traceMapper;
    @Resource
    private TraceDetailsMapper traceDetailsMapper;

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
            redisService.set(CacheConstants.DRAW_ACTIVITY_PREFIX + id,
                    JSON.toJSONString(drawActivity), 60 * 60 * 24 * 30);
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
     *
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
        if (!"2bfdd1cc48ac96a9".equalsIgnoreCase(actCode)) {
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

        // 一个用户只能抽奖一次
        Long canDrawCount = redisService.incr("DAY_CAN_DRAW_COUNT", 0);
        logger.info("======缓存可抽奖次数" + canDrawCount);
        if (canDrawCount == null || canDrawCount <= 0) {
            canDrawCount = 1L;
        }
        String dataStr = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        Long drawCount = redisService.incr(CacheConstants.DRAW_DATE_COUNT + dataStr + userId, 1, 1, TimeUnit.DAYS);
        logger.info("======抽奖用户次数userId=" + userId + "，抽奖" + drawCount);
        Asserts.assertFalse(drawCount > canDrawCount, 502, "您每天只能抽奖" + canDrawCount + "次，请明天再继续抽奖");
        //redisService.expire(CacheConstants.DRAW_DATE_COUNT + dataStr + userId, 60 * 60 * 24);

        Map<Integer, List<YfDrawPrizeResult>> prizeMap = prizeList.stream().collect(Collectors
                .groupingBy(YfDrawPrizeResult::getPrizeLevel));
        Integer prizeLevel = 3;
        YfDrawPrizeResult firstPrize = prizeMap.get(1).get(0);
        YfDrawPrizeResult secondPrize = prizeMap.get(2).get(0);
        YfDrawPrizeResult thirdPrize = prizeMap.get(3).get(0);

        // 根据ip查询地址, 找不到归属地默认抽到三等奖
        YfUserCouponResult result = new YfUserCouponResult();
        result.setDrawPrizeLevel(prizeLevel);
        result.setCouponTitle(thirdPrize.getPrizeTitle());
        result.setDrawPrizeIcon(thirdPrize.getPrizeIcon());
        String region = Ip2regionUtil.getRegionByIp(ipStr);
        String location = "";
        Integer provinceId = null;
        if (StringUtils.isNotBlank(region)) {
            String[] dataArr = region.split("\\|");
            try {
                location = dataArr[2];
                provinceId = this.getProvinceByIpStr(location);
            } catch (Exception e) {

            }
            if (provinceId == null) {
                logger.info("======抽奖用户userId=" + userId + ",actCode=" + actCode + ",抽奖结果=" + JSON.toJSONString(result));
                frontUserCouponService.createUserCouponByPrize(userId, actCode, thirdPrize);
                actCodeBatchDetail.setActTitle(yfDrawActivityResult.getActTitle());
                frontDrawRecordService.saveDrawRecord(userId, actCodeBatchDetail, thirdPrize, location);
                return result;
            }
        }
        // 判断省份抽奖规则有没有走定制化, 找不到根据活动奖品概率去发奖品, 根据大盒小盒,去抽奖
        DrawProvinceRate provinceRate = this.getProvinceRateByActIdAndProvince(actCodeBatchDetail.getActId(), provinceId);
        if (provinceRate == null) {
            if (BoxSpecValEnum.BIG.getCode().equalsIgnoreCase(actCodeBatchDetail.getBoxSpecVal())) {
                prizeLevel = startDraw(userId, firstPrize.getWinRate(), firstPrize.getPrizeCount(), secondPrize.getWinRate(), secondPrize.getPrizeCount());
            } else {
                prizeLevel = startDraw(userId, firstPrize.getWinRate(), firstPrize.getPrizeCount(), secondPrize.getSmallBoxRate(), secondPrize.getPrizeCount());
            }
        } else {
            if (BoxSpecValEnum.BIG.getCode().equalsIgnoreCase(actCodeBatchDetail.getBoxSpecVal())) {
                prizeLevel = startDraw(userId, provinceRate.getFirstWinRate(), firstPrize.getPrizeCount(), provinceRate.getSecondWinRate(), secondPrize.getPrizeCount());
            } else {
                prizeLevel = startDraw(userId, provinceRate.getFirstWinRate(), firstPrize.getPrizeCount(), provinceRate.getSecondSmallBoxWinRate(), secondPrize.getPrizeCount());
            }
        }

        YfDrawPrizeResult drawPrize = prizeMap.get(prizeLevel).get(0);
        result.setDrawPrizeLevel(prizeLevel);
        result.setCouponTitle(drawPrize.getPrizeTitle());
        result.setDrawPrizeIcon(drawPrize.getPrizeIcon());

        logger.info("======抽奖用户userId=" + userId + ",actCode=" + actCode + ",抽奖结果=" + JSON.toJSONString(result));
        frontUserCouponService.createUserCouponByPrize(userId, actCode, drawPrize);
        actCodeBatchDetail.setActTitle(yfDrawActivityResult.getActTitle());
        frontDrawRecordService.saveDrawRecord(userId, actCodeBatchDetail, drawPrize, location);
        return result;
    }

    @Override
    public YfActCodeBatchDetailResult getYfActCodeBatchDetailByActCode(String actCode) throws ApiException {
        Asserts.assertStringNotBlank(actCode, 500, "请扫描正确的券码");
        YfActCodeBatchDetailResult yfActCodeBatchDetailResult = null;
        ActCodeBatchDetail actCodeBatchDetail = null;
        Object userObject = redisService.get(CacheConstants.ACT_CODE_BATCH_ACT_NO + actCode);
        if (userObject != null) {
            yfActCodeBatchDetailResult = JSON.parseObject(userObject.toString(), YfActCodeBatchDetailResult.class);
        } else {
            actCodeBatchDetail = actCodeBatchDetailMapper.selectOne(Wrappers
                    .lambdaQuery(ActCodeBatchDetail.class).eq(ActCodeBatchDetail::getActCode, actCode));
            if (actCodeBatchDetail != null) {
                yfActCodeBatchDetailResult = BeanUtil.convert(actCodeBatchDetail, YfActCodeBatchDetailResult.class);
                Trace trace = traceMapper.selectOne(Wrappers.<Trace>lambdaQuery().eq(Trace::getTraceNo, actCodeBatchDetail.getTraceNo()).orderByDesc());
                if (trace != null) {
                    yfActCodeBatchDetailResult.setBoxSpecVal("1002".equals(trace.getProductNo()) ? BoxSpecValEnum.BIG.getCode() : BoxSpecValEnum.SMALL.getCode());
                    TraceDetails traceDetails = traceDetailsMapper.selectOne(Wrappers.<TraceDetails>lambdaQuery().eq(TraceDetails::getBoxNo, trace.getBoxNo()).orderByDesc());
                    if (traceDetails != null) {
                        yfActCodeBatchDetailResult.setDealerMobile(traceDetails.getDealerMobile());
                        yfActCodeBatchDetailResult.setDealerName(traceDetails.getDealerName());
                        yfActCodeBatchDetailResult.setDealerAddress(traceDetails.getDealerAddress());
                    }

                }
            }
            redisService.set(CacheConstants.ACT_CODE_BATCH_ACT_NO + actCode,
                    JSON.toJSONString(yfActCodeBatchDetailResult), 60 * 30);
        }
        return yfActCodeBatchDetailResult;
    }

    @Override
    public Long addDrawUserWhite(Integer userId) throws ApiException {
        Asserts.assertNonNull(userId, 500, "用户id不可以为空");
        return redisService.sAdd(CacheConstants.DRAW_WHITE_USER_DATA, userId);
    }

    @Override
    public Long deleteDrawUserWhite(Integer userId) throws ApiException {
        Asserts.assertNonNull(userId, 500, "用户id不可以为空");
        return redisService.sRemove(CacheConstants.DRAW_WHITE_USER_DATA, userId);
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
     *
     * @return provinceId || null
     */
    private Integer getProvinceByIpStr(String province) {
        ProvinceEnum provinceEnum = ProvinceEnum.getByPrefix(province);
        if (provinceEnum == null) {
            return null;
        }
        return provinceEnum.getId();
//
//
//        IpAddress ipAddress = null;
//        long ipLong = NetUtil.ipv4ToLong(ipStr);
//        Object ipStrObject = redisService.get(CacheConstants.USER_REQUEST_IP_STR + ipLong);
//        if (ipStrObject != null) {
//            ipAddress = JSON.parseObject(ipStrObject.toString(), IpAddress.class);
//        } else {
//            ipAddress = ipAddressMapper.selectOne(Wrappers.lambdaQuery(IpAddress.class)
//                    .le(IpAddress::getIpStartLong, ipLong).ge(IpAddress::getIpEndLong, ipLong)
//                    .orderByDesc(IpAddress::getId));
//            redisService.set(CacheConstants.USER_REQUEST_IP_STR + ipLong,
//                    JSON.toJSONString(ipAddress), 60 * 30);
//        }
//
//        // 找不到地址默认为三等奖
//        if (ipAddress == null) {
//            return null;
//        }
//
//        // 找不到归属地默认抽到三等奖
//        String provincePrefix = ipAddress.getAddress().substring(0, 2);
//        ProvinceEnum provinceEnum = ProvinceEnum.getByPrefix(provincePrefix);
//        if (provinceEnum == null) {
//            return null;
//        }
//        return provinceEnum.getId();
    }


    /**
     * 生成随机字符串抽奖
     *
     * @param firstRate   一等奖中奖概率
     * @param firstRate   一等奖中奖概率
     * @param firstCount  一等奖数量
     * @param secondRate  二等奖中奖概率
     * @param secondCount 二等奖数量
     * @return
     */
    private Integer startDraw(Integer userId, Integer firstRate, Integer firstCount, Integer secondRate, Integer secondCount) {
        Integer prizeLevel = 3, random = 1;
        Long userIsWhite = redisService.sRemove(CacheConstants.DRAW_WHITE_USER_DATA, userId);
        if (userIsWhite < 1) {
            random = new Random().nextInt(10000);
        } else {
            logger.info("====抽奖白名单userId=" + userId);
        }

        if (random <= firstRate) {
            // 校验是否超卖, 超卖的话默认抽中三等奖
            Long winCount = redisService.incr(CacheConstants.PRIZE_FIRST_WIN_COUNT, 1);
            if (winCount <= firstCount) {
                prizeLevel = 1;
            }
        } else if (random <= (firstRate + secondRate)) {
            Long winCount = redisService.incr(CacheConstants.PRIZE_SECOND_WIN_COUNT, 1);
            if (winCount <= secondCount) {
                prizeLevel = 2;
            }
        }
        logger.info("抽中了" + prizeLevel + "等奖");
        return prizeLevel;
    }

}

