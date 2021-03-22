package com.yfshop.code.manager.impl;

import com.yfshop.code.model.UserAddress;
import com.yfshop.code.mapper.UserAddressMapper;
import com.yfshop.code.manager.UserAddressManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户收货地址 服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Service
public class UserAddressManagerImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressManager {

}
