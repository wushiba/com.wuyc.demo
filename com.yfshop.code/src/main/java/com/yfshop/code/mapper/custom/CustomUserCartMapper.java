package com.yfshop.code.mapper.custom;

import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户购物车 Mapper 接口
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
public interface CustomUserCartMapper {

    int addCartNum(@Param("userId") Integer userId, @Param("skuId") Integer skuId, @Param("num") int num);

    int updateCartNum(@Param("userId") Integer userId, @Param("skuId") Integer skuId, @Param("num") int num);
}
