package com.yfshop.shop.dao;

import org.apache.ibatis.annotations.Param;

/**
 * @author wuyc
 * created 2021/4/7 14:36
 **/
public interface UserCouponDao {

    int updateUserCouponInUse(@Param("userCouponId") Long userCouponId);

}
