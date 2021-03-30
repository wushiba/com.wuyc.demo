package com.yfshop.shop.service.impl;

import cn.hutool.core.net.NetUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.BoxSpecValEnum;
import com.yfshop.common.enums.ProvinceEnum;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.result.*;
import com.yfshop.shop.service.ActivityActCodeBatchService;
import com.yfshop.shop.service.ActivityCouponService;
import com.yfshop.shop.service.ActivityDrawService;
import com.yfshop.shop.service.ActivityUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @Title:活动抽奖Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 19:09:17
 * @Version:1.1.0
 */
@Service
public class ActivityDrawServiceImpl implements ActivityDrawService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityDrawServiceImpl.class);

    @Resource
    private DrawPrizeMapper drawPrizeMapper;

    @Resource
    private IpAddressMapper ipAddressMapper;

    @Resource
    private DrawActivityMapper drawActivityMapper;

    @Resource
    private DrawProvinceRateMapper drawProvinceRateMapper;

    @Resource
    private ActCodeBatchDetailMapper actCodeBatchDetailMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private ActivityUserService activityUserService;

    @Resource
    private ActivityActCodeBatchService activityActCodeBatchService;

    @Resource
    private ActivityCouponService activityCouponService;

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
        YfUserResult user = activityUserService.getUserById(userId);
        Asserts.assertNonNull(user, 500, "用户不存在,请先授权关注公众号");

        YfActCodeBatchDetailResult actCodeBatchDetail = activityActCodeBatchService.getYfActCodeBatchDetailByActCode(actCode);
        Asserts.assertNonNull(actCodeBatchDetail, 500, "请扫描正确的券码");
        // todo 要判断是否使用

        // 获取奖品，每个奖品登记优惠券id， 可以走缓存
        Object prizeObject = redisService.get(CacheConstants.DRAW_PRIZE_NAME_PREFIX + actCodeBatchDetail.getActId());
        List<DrawPrize> prizeList = JSON.parseArray(prizeObject.toString(), DrawPrize.class);
        Asserts.assertCollectionNotEmpty(prizeList, 500, "活动暂未配置奖品，请稍微再试");
        DrawPrize firstPrize = prizeList.stream().filter(data ->
                data.getPrizeLevel() == 1).collect(Collectors.toList()).get(0);
        DrawPrize secondPrize = prizeList.stream().filter(data ->
                data.getPrizeLevel() == 2).collect(Collectors.toList()).get(0);

        // 根据ip查询地址, 找不到归属地默认抽到三等奖
        Integer provinceId = this.getProvinceByIpStr(ipStr);

        // 判断省份抽奖规则有没有走定制化, 找不到根据活动奖品概率去发奖品, 根据大盒小盒,去抽奖
        DrawProvinceRate provinceRate = this.getProvinceRateByActIdAndProvince(actCodeBatchDetail.getActId(), provinceId);
        Integer couponId = null;
        if (provinceRate == null) {
            if (BoxSpecValEnum.BIG.getCode().equalsIgnoreCase(actCodeBatchDetail.getBoxSpecVal())) {
                Integer integer = startDraw(firstPrize.getWinRate(), secondPrize.getWinRate());
            } else {
                startDraw(firstPrize.getWinRate(), secondPrize.getSmallBoxRate());
            }
        } else {
            if (BoxSpecValEnum.BIG.getCode().equalsIgnoreCase(actCodeBatchDetail.getBoxSpecVal())) {
                startDraw(provinceRate.getFirstWinRate(), provinceRate.getSecondWinRate());
            } else {
                startDraw(provinceRate.getFirstWinRate(), provinceRate.getSecondSmallBoxWinRate());
            }
        }

        activityCouponService.createUserCoupon(userId, firstPrize.getCouponId());
        return null;
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

