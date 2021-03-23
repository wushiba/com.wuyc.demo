package com.yfshop.admin.config;

import cn.dev33.satoken.stp.StpInterface;
import com.google.common.collect.Lists;
import com.yfshop.admin.api.rolepermission.RolePermissionManageService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限配置
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @DubboReference(check = false)
    private RolePermissionManageService rolePermissionManageService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginKey) {
        return rolePermissionManageService.queryMerchantPermissions(Integer.valueOf(loginId.toString()));
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginKey) {
        String role = rolePermissionManageService.findMerchantRole(Integer.valueOf(loginId.toString()));
        return Lists.newArrayList(role);
    }

}