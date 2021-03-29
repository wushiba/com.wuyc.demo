package com.yfshop.admin.dao;

import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户购物车 Mapper 接口
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
public interface UserCartDao {

    int addCartNum(@Param("userId") Integer userId, @Param("skuId") Integer skuId, @Param("num") int num);

    int updateCartNum(@Param("userId") Integer userId, @Param("skuId") Integer skuId, @Param("num") int num);
}
