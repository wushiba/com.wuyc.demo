package com.yfshop.code.mapper;

import com.yfshop.code.model.UserCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户购物车 Mapper 接口
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
public interface UserCartMapper extends BaseMapper<UserCart> {
    int addCartNum(@Param("userId") Integer userId, @Param("skuId") Integer skuId, @Param("num") int num);
    int updateCartNum(@Param("userId") Integer userId, @Param("skuId") Integer skuId, @Param("num") int num);
}
