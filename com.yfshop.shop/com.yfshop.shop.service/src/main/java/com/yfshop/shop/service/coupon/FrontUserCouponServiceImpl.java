package com.yfshop.shop.service.coupon;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.UserCouponMapper;
import com.yfshop.code.model.UserCoupon;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;
import com.yfshop.shop.service.coupon.service.FrontUserCouponService;
import org.apache.dubbo.config.annotation.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Title:用户优惠券Service实现
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-23 16:24:25
 * @Version:1.1.0
 */
@Service(dynamic = true)
public class FrontUserCouponServiceImpl implements FrontUserCouponService {

    @Resource
    private UserCouponMapper userCouponMapper;

    /**
     * 查询用户优惠券YfUserCoupon
     * @param userId	用户id
     * @param isCanUse	是否可用， 可用传Y， 不可用传N
     * @return
     * @throws ApiException
     */
    @Override
    public List<YfUserCouponResult> findUserCanUseCouponList(Integer userId, String isCanUse) throws ApiException {
        LambdaQueryWrapper<UserCoupon> queryWrapper = Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getUserId, userId);

        if ("Y".equalsIgnoreCase(isCanUse)) {
            queryWrapper.eq(UserCoupon::getUseStatus, "Y")
                    .gt(UserCoupon::getValidEndTime, new Date());
        } else {
            queryWrapper.eq(UserCoupon::getUseStatus, "N")
                    .lt(UserCoupon::getValidEndTime, new Date());
        }
        queryWrapper.orderByDesc(UserCoupon::getId);
        List<UserCoupon> dataList = userCouponMapper.selectList(queryWrapper);
        return BeanUtil.convertList(dataList, YfUserCouponResult.class);
    }

}

