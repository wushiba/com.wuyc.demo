package com.yfshop.admin.service.coupon;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.coupon.request.QueryUserCouponReq;
import com.yfshop.admin.api.coupon.result.CouponRulesResult;
import com.yfshop.admin.api.coupon.result.YfUserCouponResult;
import com.yfshop.admin.api.coupon.service.AdminCouponService;
import com.yfshop.admin.api.coupon.service.AdminUserCouponService;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.enums.UserCouponStatusEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.util.BeanUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title:用户优惠券Service实现
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-23 16:24:25
 * @Version:1.1.0
 */
@DubboService
public class YfUserCouponServiceImpl implements AdminUserCouponService {

    @Resource
    private UserCouponMapper userCouponMapper;
    @Resource
    private CouponMapper couponMapper;
    @Autowired
    private AdminCouponService adminCouponService;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public YfUserCouponResult getYfUserCouponById(Integer id) throws ApiException {
        if (id == null || id <= 0) return null;
        YfUserCouponResult userCouponResult = null;
        UserCoupon userCoupon = userCouponMapper.selectById(id);
        if (userCoupon != null) {
            userCouponResult = new YfUserCouponResult();
            BeanUtil.copyProperties(userCoupon, userCouponResult);
        }
        return userCouponResult;
    }

    @Override
    public Page<YfUserCouponResult> findYfUserCouponListByPage(QueryUserCouponReq req) throws ApiException {
        LambdaQueryWrapper<UserCoupon> queryWrapper = Wrappers.lambdaQuery(UserCoupon.class)
                .eq(req.getCouponId() != null, UserCoupon::getCouponId, req.getCouponId())
                .eq(req.getUseStatus() != null, UserCoupon::getUseStatus, req.getUseStatus())
                .eq(req.getOrderId() != null, UserCoupon::getOrderId, req.getOrderId())
                .eq(req.getUserName() != null, UserCoupon::getNickname, req.getUserName())
                .orderByDesc(UserCoupon::getId);
        Page<UserCoupon> itemPage = userCouponMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()), queryWrapper);
        Page<YfUserCouponResult> page = new Page<>(itemPage.getCurrent(), itemPage.getSize(), itemPage.getTotal());
        page.setRecords(BeanUtil.convertList(itemPage.getRecords(), YfUserCouponResult.class));
        return page;
    }

    @Override
    public List<YfUserCouponResult> getAll(QueryUserCouponReq req) throws ApiException {
        LambdaQueryWrapper<UserCoupon> queryWrapper = Wrappers.lambdaQuery(UserCoupon.class)
                .eq(req.getCouponId() != null, UserCoupon::getCouponId, req.getCouponId())
                .eq(req.getUseStatus() != null, UserCoupon::getUseStatus, req.getUseStatus())
                .eq(req.getOrderId() != null, UserCoupon::getOrderId, req.getOrderId())
                .orderByDesc(UserCoupon::getId);

        List<UserCoupon> dataList = userCouponMapper.selectList(queryWrapper);
        return BeanUtil.convertList(dataList, YfUserCouponResult.class);
    }

    /**
     * 支付成功后调用发券逻辑
     *
     * @param orderId
     * @throws ApiException
     */
    @Override
    @Async
    public void sendUserCoupon(Long orderId) throws ApiException {
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(Wrappers.<OrderDetail>lambdaQuery().
                eq(OrderDetail::getOrderId, orderId));
        List<CouponRulesResult> couponRulesResults = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(orderDetailList)) {
            Map<String, CouponRulesResult> temp = new HashMap<>();
            Integer userId = orderDetailList.get(0).getUserId();
            User user = userMapper.selectById(userId);
            List<CouponRulesResult> couponRulesResultList = adminCouponService.getCouponRulesList();
            for (CouponRulesResult result : couponRulesResultList) {
                //通用直接获取订单支付总额
                if ("ALL".equals(result.getItemIds())) {
                    Order order = orderMapper.selectById(orderId);
                    if (order.getPayPrice().compareTo(result.getConditions()) >= 0) {
                        couponRulesResults.add(result);
                    }
                } else {
                    //判断是否支付金额是否满足发券逻辑
                    BigDecimal bigDecimal = null;
                    for (OrderDetail item : orderDetailList) {
                        if (result.getItemIds().contains(item.getItemId() + "")) {
                            if (bigDecimal == null) {
                                bigDecimal = item.getPayPrice();
                            } else {
                                bigDecimal = bigDecimal.add(item.getPayPrice());
                            }
                        }
                    }
                    if (bigDecimal != null && bigDecimal.compareTo(result.getConditions()) >= 0) {
                        CouponRulesResult t = temp.get(result.getItemIds());
                        if (t == null || result.getConditions().compareTo(t.getConditions()) > 0) {
                            temp.put(result.getItemIds(), result);
                        }
                    }
                }
            }
            temp.forEach((key, value) -> {
                couponRulesResults.add(value);
            });
            couponRulesResults.forEach(item -> {
                Integer count = 0;
                if (item.getLimitCount() > 0) {
                    count = userCouponMapper.selectCount(Wrappers.lambdaQuery(UserCoupon.class)
                            .eq(UserCoupon::getUserId, userId)
                            .eq(UserCoupon::getCouponId, item.getCouponId()));
                }
                //判断卡券是否上限
                if (count == 0 || count < item.getLimitCount()) {
                    Coupon coupon = couponMapper.selectById(item.getCouponId());
                    UserCoupon userCoupon = new UserCoupon();
                    userCoupon.setCreateTime(LocalDateTime.now());
                    userCoupon.setUpdateTime(LocalDateTime.now());
                    userCoupon.setUserId(userId);
                    userCoupon.setCouponId(coupon.getId());
                    userCoupon.setCouponTitle(coupon.getCouponTitle());
                    userCoupon.setCouponResource(coupon.getCouponResource());
                    userCoupon.setCouponPrice(coupon.getCouponPrice());
                    userCoupon.setUseConditionPrice(coupon.getUseConditionPrice());
                    userCoupon.setUseRangeType(coupon.getUseRangeType());
                    userCoupon.setCanUseItemIds(coupon.getCanUseItemIds());
                    userCoupon.setValidStartTime(LocalDateTime.now());
                    userCoupon.setValidEndTime(coupon.getValidEndTime());
                    if (user != null) {
                        userCoupon.setNickname(user.getNickname());
                    }
                    userCoupon.setUseStatus(UserCouponStatusEnum.NO_USE.getCode());
                    userCoupon.setSrcOrderId(orderId);
                    userCoupon.setCouponDesc(coupon.getCouponDesc());
                    userCouponMapper.insert(userCoupon);
                }
            });
        }
    }

}

