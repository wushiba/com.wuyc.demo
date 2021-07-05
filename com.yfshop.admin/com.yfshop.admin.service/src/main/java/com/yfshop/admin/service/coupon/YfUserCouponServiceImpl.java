package com.yfshop.admin.service.coupon;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.coupon.request.QueryUserCouponReq;
import com.yfshop.admin.api.coupon.result.YfUserCouponResult;
import com.yfshop.admin.api.coupon.service.AdminUserCouponService;
import com.yfshop.code.mapper.UserCouponMapper;
import com.yfshop.code.model.UserCoupon;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.util.BeanUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.util.List;

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

    @Override
    @Async
    public void sendUserCoupon(Long orderId) throws ApiException {

    }


}

