package com.yfshop.shop.service.activity;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.DrawRecordMapper;
import com.yfshop.code.mapper.TraceMapper;
import com.yfshop.code.mapper.UserCouponMapper;
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
import java.util.stream.Collectors;

/**
 * @Title:活动抽奖Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 19:09:17
 * @Version:1.1.0
 */
@DubboService
public class FrontDrawRecordServiceImpl implements FrontDrawRecordService {
    private static final Logger logger = LoggerFactory.getLogger(FrontDrawRecordServiceImpl.class);
    @Resource
    private DrawRecordMapper drawRecordMapper;
    @Resource
    private UserCouponMapper userCouponMapper;
    @Resource
    private FrontUserService frontUserService;

    /**
     * 保存中奖记录
     *
     * @param userId
     * @param actCodeBatchDetailResult
     * @param drawPrize
     */
    @Async
    @Override
    public void saveDrawRecord(Integer userId, Long userCouponId, YfActCodeBatchDetailResult actCodeBatchDetailResult, YfDrawPrizeResult drawPrize) {
        DrawRecord drawRecord = new DrawRecord();
        drawRecord.setUserCouponId(userCouponId);
        drawRecord.setActId(actCodeBatchDetailResult.getActId());
        drawRecord.setActTitle(actCodeBatchDetailResult.getActTitle());
        drawRecord.setPrizeLevel(drawPrize.getPrizeLevel());
        drawRecord.setPrizeTitle(drawPrize.getPrizeTitle());
        drawRecord.setUserId(userId);
        UserResult userResult = frontUserService.getUserById(userId);
        if (userResult != null) {
            drawRecord.setUserName(userResult.getNickname());
        }
        drawRecord.setUserLocation(actCodeBatchDetailResult.getLocation());
        drawRecord.setUseStatus(UserCouponStatusEnum.NO_USE.getCode());
        drawRecord.setSpec(BoxSpecValEnum.BIG.getCode().equals(actCodeBatchDetailResult.getBoxSpecVal()) ? "大盒" : "小盒");
        drawRecord.setActCode(actCodeBatchDetailResult.getActCode());
        drawRecord.setTraceNo(actCodeBatchDetailResult.getTraceNo());
        drawRecord.setDealerName(actCodeBatchDetailResult.getDealerName());
        drawRecord.setDealerAddress(actCodeBatchDetailResult.getDealerAddress());
        drawRecord.setIp(actCodeBatchDetailResult.getIp());
        drawRecord.setIpRegion(actCodeBatchDetailResult.getIpRegion());
        drawRecordMapper.insert(drawRecord);
    }

    @Override
    @Async
    public void updateDrawRecord(Long userCoupId, String useStatus, String userName, String userMobile) {
        DrawRecord drawRecord = new DrawRecord();
        drawRecord.setUseStatus(useStatus);
        drawRecord.setUserMobile(userMobile);
        drawRecord.setUserName(userName);
        drawRecordMapper.update(drawRecord, Wrappers.<DrawRecord>lambdaQuery().eq(DrawRecord::getUserCouponId, userCoupId));
    }

    @Override
    public void updateDrawRecordUseStatus(Long userCoupId, String useStatus) {
        DrawRecord drawRecord = new DrawRecord();
        drawRecord.setUseStatus(useStatus);
        drawRecordMapper.update(drawRecord, Wrappers.<DrawRecord>lambdaQuery()
                .eq(DrawRecord::getUserCouponId, userCoupId));

    }
}

