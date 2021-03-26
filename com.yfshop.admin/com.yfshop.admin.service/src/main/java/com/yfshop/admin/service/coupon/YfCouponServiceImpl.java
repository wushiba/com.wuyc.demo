package com.yfshop.admin.service.coupon;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.coupon.request.CreateCouponReq;
import com.yfshop.admin.api.coupon.request.QueryCouponReq;
import com.yfshop.admin.api.coupon.result.YfCouponResult;
import com.yfshop.admin.api.coupon.service.AdminCouponService;
import com.yfshop.code.mapper.CouponMapper;
import com.yfshop.code.model.Coupon;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.common.util.DateUtil;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title:平台优惠券Service实现
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-23 13:47:17
 * @Version:1.1.0
 */
@Service(dynamic = true)
public class YfCouponServiceImpl implements AdminCouponService {

    @Resource
    private CouponMapper couponMapper;

    @Override
    public YfCouponResult getYfCouponById(Integer id) throws ApiException {
        if (id == null || id <= 0) return null;
        YfCouponResult yfCouponResult = null;
        Coupon coupon = couponMapper.selectById(id);
        if (coupon != null) {
            yfCouponResult = new YfCouponResult();
            BeanUtil.copyProperties(coupon, yfCouponResult);
        }
        return yfCouponResult;
    }

    @Override
    public Page<YfCouponResult> findYfCouponListByPage(QueryCouponReq req) throws ApiException {
        Coupon coupon = BeanUtil.convert(req, Coupon.class);
        Page<Coupon> page = new Page<>(req.getPageIndex(), req.getPageSize());
        LambdaQueryWrapper<Coupon> queryWrapper = Wrappers.<Coupon>lambdaQuery().setEntity(coupon);
        Page<Coupon> pageData = couponMapper.selectPage(page, queryWrapper);

        Page<YfCouponResult> data = new Page<>(req.getPageIndex(), req.getPageSize(), page.getTotal());
        data.setRecords(BeanUtil.convertList(pageData.getRecords(), YfCouponResult.class));
        return data;
    }

    @Override
    public List<YfCouponResult> getAll(QueryCouponReq req) throws ApiException {
        Coupon coupon = BeanUtil.convert(req, Coupon.class);
        LambdaQueryWrapper<Coupon> queryWrapper = Wrappers.<Coupon>lambdaQuery().setEntity(coupon);
        List<Coupon> dataList = couponMapper.selectList(queryWrapper);
        return BeanUtil.convertList(dataList, YfCouponResult.class);
    }

    @Override
    public void insertYfCoupon(CreateCouponReq couponReq) throws ApiException {
        checkCouponParams(couponReq);
        Coupon coupon = BeanUtil.convert(couponReq, Coupon.class);
        coupon.setCreateTime(LocalDateTime.now());

//        if ("DATE_RANGE".equalsIgnoreCase(couponReq.getValidType())) {
//            coupon.setValidStartTime(DateUtil.dateToLocalDateTime(couponReq.getValidStartTime()));
//            coupon.setValidEndTime(DateUtil.dateToLocalDateTime(couponReq.getValidEndTime()));
//        }
        coupon.setIsDelete("N");
        coupon.setIsEnable("N");
        couponMapper.insert(coupon);
    }

    @Override
    public void updateYfCoupon(CreateCouponReq couponReq) throws ApiException {
        checkCouponParams(couponReq);
        Coupon coupon = BeanUtil.convert(couponReq, Coupon.class);
        couponMapper.updateById(coupon);
    }

    @Override
    public void deleteYfCoupon(Integer couponId) throws ApiException {
        Coupon coupon = couponMapper.selectById(couponId);
        Asserts.assertNonNull(coupon, 500, "优惠券不存在");
        couponMapper.deleteById(couponId);
    }

    @Override
    public void updateCouponStatus(Integer couponId, String isEnable) throws ApiException {
        Coupon coupon = couponMapper.selectById(couponId);
        Asserts.assertNonNull(coupon, 500, "优惠券不存在");
        coupon.setIsEnable(isEnable);
        couponMapper.updateById(coupon);
    }

    public void checkCouponParams(CreateCouponReq couponReq) {
        // DATE_RANGE(日期范围), TODAY(领取当天), FIX_DAY(固定天数)
        String validType = couponReq.getValidType();
        if ("DATE_RANGE".equalsIgnoreCase(validType)) {
            Asserts.assertNonNull(couponReq.getValidStartTime(), 500, "开始日期不可以为空");
            Asserts.assertNonNull(couponReq.getValidEndTime(), 500, "结束日期不可以为空");
            couponReq.setValidDay(null);
        } else if ("FIX_DAY".equalsIgnoreCase(validType)) {
            Asserts.assertFalse(couponReq.getValidDay() == null || couponReq.getValidDay() <= 0,
                    500, "领取后有效日期不可以为空");
            couponReq.setValidStartTime(null);
            couponReq.setValidEndTime(null);
        } else {
            couponReq.setValidStartTime(null);
            couponReq.setValidEndTime(null);
            couponReq.setValidDay(null);
        }

        String useRangeType = couponReq.getUseRangeType();
        if ("ALL".equalsIgnoreCase(useRangeType)) {
            couponReq.setCanUseItemIds(null);
        } else if ("ITEM".equalsIgnoreCase(useRangeType)) {
            Asserts.assertStringNotBlank(couponReq.getCanUseItemIds(), 500, "请选择可使用的商品");
        }
    }

}

