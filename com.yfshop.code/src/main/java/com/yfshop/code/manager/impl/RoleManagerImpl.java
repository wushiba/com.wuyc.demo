package com.yfshop.code.manager.impl;

import com.yfshop.code.model.Role;
import com.yfshop.code.mapper.RoleMapper;
import com.yfshop.code.manager.RoleManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Service
public class RoleManagerImpl extends ServiceImpl<RoleMapper, Role> implements RoleManager {

}
