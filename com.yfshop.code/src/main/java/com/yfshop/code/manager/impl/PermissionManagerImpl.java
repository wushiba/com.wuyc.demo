package com.yfshop.code.manager.impl;

import com.yfshop.code.model.Permission;
import com.yfshop.code.mapper.PermissionMapper;
import com.yfshop.code.manager.PermissionManager;
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
public class PermissionManagerImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionManager {

}
