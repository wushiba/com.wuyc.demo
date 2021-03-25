package com.yfshop.shop.service.impl;

import cn.hutool.core.net.NetUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.BoxSpecValEnum;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.shop.enums.ProvinceEnum;
import com.yfshop.shop.service.ActivityCouponService;
import com.yfshop.shop.service.ActivityDrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
@Service
public class ActivityDrawServiceImpl implements ActivityDrawService {

    @Autowired
    private UserMapper userMapper;

    @Resource
    private DrawPrizeMapper drawPrizeMapper;

    @Resource
    private IpAddressMapper ipAddressMapper;

    @Resource
    private UserCouponMapper userCouponMapper;

    @Resource
    private ActivityCouponService activityCouponService;

    @Resource
    private DrawProvinceRateMapper drawProvinceRateMapper;

    @Resource
    private ActCodeBatchDetailMapper actCodeBatchDetailMapper;

    @Resource
    private RedisService redisService;

    /**
     * 用户抽奖
     *
     * @param userId  用户id
     * @param ipStr   用户当前所在id
     * @param actCode 活动码
     * @throws ApiException
     */
    @Override
    // TODO: 2021/3/25 某些数据要存缓存的
    public void createUserCoupon(Integer userId, String ipStr, String actCode) throws ApiException {
        User user = userMapper.selectById(userId);
        Asserts.assertNonNull(user, 500, "用户不存在,请先授权关注公众号");

        Asserts.assertStringNotBlank(actCode, 500, "请扫描正确的券码");
        ActCodeBatchDetail actCodeBatchDetail = actCodeBatchDetailMapper.selectOne(Wrappers
                .lambdaQuery(ActCodeBatchDetail.class).eq(ActCodeBatchDetail::getActCode, actCode));
        Asserts.assertNonNull(actCodeBatchDetail, 500, "请扫描正确的券码");

        // 获取奖品，每个奖品登记优惠券id， 可以走缓存
        List<DrawPrize> prizeList = null;
        Object prizeObject = redisService.get(CacheConstants.DRAW_PRIZE_NAME_PREFIX + actCodeBatchDetail.getActId());
        if (prizeObject != null) {
            prizeList = JSON.parseArray(JSON.toJSONString(prizeObject), DrawPrize.class);
        } else {
            prizeList = drawPrizeMapper.selectList(Wrappers.lambdaQuery(DrawPrize.class)
                    .eq(DrawPrize::getActId, actCodeBatchDetail.getActId()));
            redisService.set(CacheConstants.DRAW_PRIZE_NAME_PREFIX + actCodeBatchDetail.getActId(),
                    JSON.toJSONString(prizeList), 180 * 24 * 60 * 1000);
        }

        DrawPrize firstPrize = prizeList.stream().filter(data ->
                data.getPrizeLevel() == 1).collect(Collectors.toList()).get(0);
        DrawPrize secondPrize = prizeList.stream().filter(data ->
                data.getPrizeLevel() == 2).collect(Collectors.toList()).get(0);
        DrawPrize thirdPrize = prizeList.stream().filter(data ->
                data.getPrizeLevel() == 3).collect(Collectors.toList()).get(0);

        // 根据ip查询地址
        long ipLong = NetUtil.ipv4ToLong(ipStr);
        IpAddress ipAddress = ipAddressMapper.selectOne(Wrappers
                .lambdaQuery(IpAddress.class).ge(IpAddress::getIpStartLong, ipLong)
                .lt(IpAddress::getIpEndLong, ipLong).orderByDesc(IpAddress::getId));
        if (ipAddress == null) {
            // 找不到ip默认抽到三等奖
            activityCouponService.createUserCoupon(userId, thirdPrize.getCouponId());
            return;
        }

        String provincePrefix = ipAddress.getAddress().substring(0, 2);
        ProvinceEnum provinceEnum = ProvinceEnum.getByPrefix(provincePrefix);
        if (provinceEnum == null) {
            // 找不到归属地默认抽到三等奖
            activityCouponService.createUserCoupon(userId, thirdPrize.getCouponId());
            return;
        }

        // 判断省份抽奖规则有没有走定制化, 这个查询也可以走缓存, 根据大盒小盒,去抽奖
        DrawProvinceRate provinceRate = drawProvinceRateMapper.selectOne(Wrappers
                .lambdaQuery(DrawProvinceRate.class)
                .eq(DrawProvinceRate::getActId, actCodeBatchDetail.getActId())
                .eq(DrawProvinceRate::getProvinceId, provinceEnum.getId()));
        if (provinceRate == null) {
            if (BoxSpecValEnum.BIG.getCode().equalsIgnoreCase(actCodeBatchDetail.getBoxSpecVal())) {
                startDraw(firstPrize.getWinRate(), secondPrize.getWinRate());
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
        return prizeLevel;
    }

}

