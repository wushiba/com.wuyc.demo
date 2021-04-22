package com.yfshop.admin.dao;

import com.yfshop.code.model.UserCoupon;

import java.util.List;

public interface UserCouponDao {
    List<UserCoupon> getUserCouponExpired(Integer day);
}
