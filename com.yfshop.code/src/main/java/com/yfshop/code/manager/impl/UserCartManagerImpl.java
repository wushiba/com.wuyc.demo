package com.yfshop.code.manager.impl;

import com.yfshop.code.model.UserCart;
import com.yfshop.code.mapper.UserCartMapper;
import com.yfshop.code.manager.UserCartManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户购物车 服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Service
public class UserCartManagerImpl extends ServiceImpl<UserCartMapper, UserCart> implements UserCartManager {

}
