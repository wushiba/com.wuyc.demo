package com.yfshop.shop.service.coupon;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.CouponResourceEnum;
import com.yfshop.common.enums.UserCouponStatusEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.dao.UserCouponDao;
import com.yfshop.shop.service.activity.result.YfActCodeBatchDetailResult;
import com.yfshop.shop.service.activity.result.YfDrawPrizeResult;
import com.yfshop.shop.service.activity.service.FrontDrawRecordService;
import com.yfshop.shop.service.coupon.request.QueryUserCouponReq;
import com.yfshop.shop.service.coupon.result.YfCouponResult;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;
import com.yfshop.shop.service.coupon.service.FrontUserCouponService;
import com.yfshop.shop.service.user.result.UserResult;
import com.yfshop.shop.service.user.service.FrontUserService;
import com.yfshop.wx.api.service.MpService;
import io.swagger.models.auth.In;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
@DubboService
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
    private FrontDrawRecordService frontDrawRecordService;
    @Resource
    private FrontUserService frontUserService;
    @Resource
    private DrawRecordMapper drawRecordMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private MerchantMapper merchantMapper;
    @DubboReference
    private MpService mpService;
    @Value("${shop.url}")
    private String shopUrl;

    /**
     * 根据id查询优惠券信息
     *
     * @param couponId 优惠券id
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
     *
     * @param userCouponReq 查询条件
     * @return
     * @throws ApiException
     */
    @Override
    public List<YfUserCouponResult> findUserCouponList(QueryUserCouponReq userCouponReq) throws ApiException {
        Asserts.assertNonNull(userCouponReq.getUserId(), 500, "用户id不可以为空");

        LambdaQueryWrapper<UserCoupon> queryWrapper = Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getUserId, userCouponReq.getUserId()).ne(UserCoupon::getCouponResource,"JD");

        if ("Y".equalsIgnoreCase(userCouponReq.getIsCanUse())) {
            queryWrapper.eq(UserCoupon::getUseStatus, UserCouponStatusEnum.NO_USE.getCode())
                    .gt(UserCoupon::getValidEndTime, new Date())
                    .lt(UserCoupon::getValidStartTime, new Date());
        } else if ("N".equalsIgnoreCase(userCouponReq.getIsCanUse())) {
            queryWrapper.in(UserCoupon::getUseStatus, UserCouponStatusEnum.IN_USE.getCode()
                    , UserCouponStatusEnum.HAS_USE.getCode())
                    .or().lt(UserCoupon::getValidEndTime, new Date());
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
        List<UserCoupon> dataList = userCouponMapper.selectList(queryWrapper.orderByDesc(UserCoupon::getCouponPrice));
        List<YfUserCouponResult> resultList = BeanUtil.convertList(dataList, YfUserCouponResult.class);

        if (userCouponReq.getItemId() == null) {
            return resultList;
        }
        return resultList.stream().filter(data -> "ALL".equalsIgnoreCase(data.getUseRangeType()) ||
                data.getCanUseItemIds().contains(userCouponReq.getItemId() + "")).collect(Collectors.toList());
    }


    @Override
    public List<YfUserCouponResult> getUserCouponAll(QueryUserCouponReq userCouponReq) throws ApiException {
        LambdaQueryWrapper<UserCoupon> queryWrapper = Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getUserId, userCouponReq.getUserId()).ne(UserCoupon::getCouponResource, "JD").orderByDesc(UserCoupon::getCouponDesc);
        List<UserCoupon> dataList = userCouponMapper.selectList(queryWrapper);
        List<YfUserCouponResult> resultList = BeanUtil.convertList(dataList, YfUserCouponResult.class);
        resultList.forEach(item -> {
            //获取自提门店详细
            if ((UserCouponStatusEnum.HAS_USE.getCode().equals(item.getUseStatus()) || UserCouponStatusEnum.IN_USE.getCode().equals(item.getUseStatus())) && item.getOrderId() != null) {
                OrderDetail orderDetail = orderDetailMapper.selectOne(Wrappers.lambdaQuery(OrderDetail.class).eq(OrderDetail::getOrderId, item.getOrderId()));
                if (orderDetail != null) {
                    item.setOrderDetailId(orderDetail.getId());
                    if (orderDetail.getMerchantId() != null) {
                        Merchant merchant = merchantMapper.selectById(orderDetail.getMerchantId());
                        if (merchant != null) {
                            item.setMerchantName(merchant.getMerchantName());
                        }
                    }
                }
            }
        });
        return resultList;
    }

    @Override
    public List<YfUserCouponResult> findAllUserDrawRecordList(QueryUserCouponReq userCouponReq) throws ApiException {
        Object couponListObject = redisService.get(CacheConstants.ALL_USER_COUPON_RECORD_LIST);
        if (couponListObject != null) {
            return JSON.parseArray(couponListObject.toString(), YfUserCouponResult.class);
        }

        LambdaQueryWrapper<UserCoupon> queryWrapper = Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getCouponResource, userCouponReq.getCouponResource())
                .ne(UserCoupon::getCouponResource, "JD")
                .orderByDesc(UserCoupon::getId);
        Page<UserCoupon> userCouponPage = userCouponMapper.selectPage(new Page<>(1, 10), queryWrapper);
        List<YfUserCouponResult> resultList = BeanUtil.convertList(userCouponPage.getRecords(), YfUserCouponResult.class);

        redisService.set(CacheConstants.ALL_USER_COUPON_RECORD_LIST, JSON.toJSONString(resultList), 60 * 2);
        return resultList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void useUserCoupon(Long userCouponId) throws ApiException {
        Asserts.assertNonNull(userCouponId, 500, "用户优惠券id不可以为空");
        int result = userCouponDao.updateUserCouponInUse(userCouponId);
        Asserts.assertFalse(result < 1, 500, "优惠券使用失败，不可以重复使用");
        frontDrawRecordService.updateDrawRecordUseStatus(userCouponId, UserCouponStatusEnum.IN_USE.getCode());
    }

    @Override
    public void updateCouponData(Long userCouponId, Long orderId, String mobile) throws ApiException {
        try {
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setId(userCouponId);
            userCoupon.setOrderId(orderId);
            userCoupon.setMobile(mobile);
            userCoupon.setUseTime(LocalDateTime.now());
            userCouponMapper.updateById(userCoupon);
            DrawRecord drawRecord = new DrawRecord();
            drawRecord.setUserMobile(mobile);
            drawRecordMapper.update(drawRecord, Wrappers.<DrawRecord>lambdaQuery()
                    .eq(DrawRecord::getUserCouponId, userCouponId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCouponOrderOrderId(Long userCouponId, Long childOrderId) throws ApiException {
        Asserts.assertNonNull(userCouponId, 500, "用户优惠券id不可以为空");
        Asserts.assertNonNull(childOrderId, 500, "订单id不可以为空");
    }


    /**
     * 用户抽中优惠券后生成优惠券
     *
     * @param userId          用户id
     * @param drawPrizeResult 奖品信息
     * @return
     * @throws ApiException
     */
    @Async
    @Override
    public YfUserCouponResult createUserCouponByPrize(Integer userId, YfActCodeBatchDetailResult actCodeBatchDetailResult, YfDrawPrizeResult drawPrizeResult) throws ApiException {
        logger.info("======开始创建优惠券用户userId=" + userId + ",actCode=" + actCodeBatchDetailResult.getActCode() + ",开始创建优惠券");
        User user = userMapper.selectById(userId);
        Asserts.assertNonNull(user, 500, "用户不存在,请先授权关注公众号");

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
        userCoupon.setActCode(actCodeBatchDetailResult.getActCode());
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
        userCoupon.setMobile(user.getMobile());
        userCoupon.setNickname(user.getNickname());
        userCoupon.setUseStatus(UserCouponStatusEnum.NO_USE.getCode());
        userCouponMapper.insert(userCoupon);
        logger.info("======结束创建优惠券用户userId=" + userId + ",actCode=" + actCodeBatchDetailResult.getActCode() + ",userCoupon=" + JSON.toJSONString(userCoupon));
        frontDrawRecordService.saveDrawRecord(userId, userCoupon.getId(), actCodeBatchDetailResult, drawPrizeResult);
        sendWinningMsg(user.getOpenId(), drawPrizeResult.getPrizeLevel(), userCoupon.getId());
        return BeanUtil.convert(userCoupon, YfUserCouponResult.class);
    }


    @Override
    public String getCouponRouteUrl(Long id) {
        UserCoupon userCoupon = userCouponMapper.selectById(id);
        if (userCoupon != null && UserCouponStatusEnum.NO_USE.getCode().equals(userCoupon.getUseStatus())) {
            String url = "";
            switch (userCoupon.getDrawPrizeLevel()) {
                case 1:
                    url = String.format("%s#/MyOrderPay?fromType=1&skuId=2030001&num=1", shopUrl);
                    break;
                case 2:
                    url = String.format("%s#/MyOrderPayForSelf?fromType=1&skuId=2032001&num=1", shopUrl);
                    break;
                default:
                    url = String.format("%s#/AllPage?id=3", shopUrl);
                    break;
            }
            return url;
        } else {
            return String.format("%s#/CouponList", shopUrl);
        }
    }

    /**
     * 发送用户中奖消息
     *
     * @param openId
     */
    private void sendWinningMsg(String openId, Integer level, Long userCouponId) {
        try {
            String first = null;
            String keyword1 = null;
            switch (level) {
                case 1:
                    first = "【点我领取奖品】恭喜您获得2元换购1688元椰岛轻奢鹿龟酒一瓶的资格。";
                    keyword1 = "1688元椰岛轻奢鹿龟酒";
                    break;
                case 2:
                    first = "【点我领取奖品】恭喜您获得2元换购椰岛135ml鹿龟酒一瓶的资格。";
                    keyword1 = "椰岛135ml鹿龟酒一瓶";
                    break;
                case 3:
                    first = "【点我领取奖品】恭喜您获得锦炉火锅30元代金券。";
                    keyword1 = "锦炉火锅30元代金券";
                    break;
            }
            if (StringUtils.isNotBlank(first) && StringUtils.isNotBlank(keyword1)) {
                List<WxMpTemplateData> data = new ArrayList<>();
                data.add(new WxMpTemplateData("first", first));
                data.add(new WxMpTemplateData("keyword1", keyword1));
                data.add(new WxMpTemplateData("keyword2", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")));
                data.add(new WxMpTemplateData("remark", "点我，在线领取奖品！"));
                WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder()
                        .templateId("pro".equalsIgnoreCase(SpringUtil.getActiveProfile()) ? "26gbak7X0fBjNlYdtXMUxHVz3N0G4bwq-xMRoe0k2FM" : "vPdtuE-E9rL-vry45AZienszFIM-lOf1ng8sTfduumU")
                        .toUser(openId)
                        .data(data)
                        .url(String.format("%s/front/route/coupon?id=%d", shopUrl, userCouponId))
                        .build();
                mpService.sendWxMpTemplateMsg(wxMpTemplateMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

