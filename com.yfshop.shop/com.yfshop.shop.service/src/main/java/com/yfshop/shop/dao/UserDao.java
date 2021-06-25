package com.yfshop.shop.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户购物车 Mapper 接口
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
public interface UserDao {

    List<String> findByIndex(@Param("id") Integer id, @Param("limit") Integer limit);
}
