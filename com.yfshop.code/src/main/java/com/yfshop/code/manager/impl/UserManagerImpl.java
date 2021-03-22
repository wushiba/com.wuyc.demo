package com.yfshop.code.manager.impl;

import com.yfshop.code.model.User;
import com.yfshop.code.mapper.UserMapper;
import com.yfshop.code.manager.UserManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Service
public class UserManagerImpl extends ServiceImpl<UserMapper, User> implements UserManager {

}
