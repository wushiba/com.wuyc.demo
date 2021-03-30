package com.yfshop.shop.dao;

import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户收货地址 Mapper 接口
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
public interface UserAddressDao {
    int disableDefaultAddress(@Param("userId") Integer userId);
}
