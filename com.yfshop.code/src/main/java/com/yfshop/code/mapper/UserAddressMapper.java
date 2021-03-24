package com.yfshop.code.mapper;

import com.yfshop.code.model.UserAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户收货地址 Mapper 接口
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
public interface UserAddressMapper extends BaseMapper<UserAddress> {

    int disableDefaultAddress(@Param("userId") Integer userId);
}
