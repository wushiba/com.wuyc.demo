package com.yfshop.admin.service.rolepermission;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.rolepermission.RolePermissionManageService;
import com.yfshop.admin.api.rolepermission.request.AssociateRolePermissionReq;
import com.yfshop.admin.api.rolepermission.request.CreatePermissionReq;
import com.yfshop.admin.api.rolepermission.result.RoleResult;
import com.yfshop.code.manager.RlRolePermissionManager;
import com.yfshop.code.mapper.MerchantMapper;
import com.yfshop.code.mapper.PermissionMapper;
import com.yfshop.code.mapper.RlRolePermissionMapper;
import com.yfshop.code.mapper.RoleMapper;
import com.yfshop.code.model.Merchant;
import com.yfshop.code.model.Permission;
import com.yfshop.code.model.RlRolePermission;
import com.yfshop.code.model.Role;
import com.yfshop.common.api.ErrorCode;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色权限管理
 *
 * @author Xulg
 * Created in 2021-03-23 14:50
 */
@DubboService
@Validated
public class RolePermissionManageServiceImpl implements RolePermissionManageService {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RlRolePermissionMapper rolePermissionMapper;
    @Resource
    private RlRolePermissionManager rolePermissionManager;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private MerchantMapper merchantMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void createPermission(Integer merchantId, @NotNull CreatePermissionReq req) throws ApiException {
        String role = findMerchantRole(merchantId);
        Asserts.assertTrue(GroupRoleEnum.SYS.getCode().equals(role), 500, "只有系统管理员能操作");
        Permission permission = new Permission();
        permission.setPid(null);
        permission.setRoleAlias(null);
        permission.setCreateTime(LocalDateTime.now());
        permission.setPermissionAlias(req.getPermissionAlias());
        permission.setPermissionName(req.getPermissionName());
        try {
            permissionMapper.insert(permission);
        } catch (DuplicateKeyException e) {
            throw new ApiException(new ErrorCode(500, req.getPermissionAlias() + "已存在"));
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void associateRolePermission(@NotNull AssociateRolePermissionReq req) throws ApiException {
        Role role = roleMapper.findByAlias(req.getRole());
        Asserts.assertNonNull(role, 500, "角色不存在");
        List<Permission> permissions = permissionMapper.findByAliases(req.getPermissions());
        Asserts.assertTrue(permissions.size() == req.getPermissions().size(), 500, "权限不存在");
        // delete old role permission relationship
        rolePermissionMapper.delete(Wrappers.lambdaQuery(RlRolePermission.class).eq(RlRolePermission::getRoleAlias, req.getRole()));
        // recreate role permission relationship
        List<RlRolePermission> rlRolePermissions = req.getPermissions().stream().map(permissionAlias -> {
            RlRolePermission rl = new RlRolePermission();
            rl.setCreateTime(LocalDateTime.now());
            rl.setRoleAlias(role.getRoleAlias());
            rl.setPermissionAlias(permissionAlias);
            return rl;
        }).collect(Collectors.toList());
        rolePermissionManager.saveBatch(rlRolePermissions);
        return null;
    }

    @Override
    public List<RoleResult> queryRoles() {
        List<Role> roles = roleMapper.selectList(null);
        if (CollectionUtil.isEmpty(roles)) {
            return new ArrayList<>(0);
        }
        Map<String, List<RlRolePermission>> rlIndexMap = rolePermissionMapper.selectList(null)
                .stream().collect(Collectors.groupingBy(RlRolePermission::getRoleAlias));
        return roles.stream().map(role -> {
            List<String> permissionAliases = rlIndexMap.getOrDefault(role.getRoleAlias(), new ArrayList<>(0))
                    .stream().map(RlRolePermission::getPermissionAlias).collect(Collectors.toList());
            RoleResult roleResult = new RoleResult();
            roleResult.setRoleAlias(role.getRoleAlias());
            roleResult.setPermissionAliases(permissionAliases);
            return roleResult;
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> queryRoleAssociationPermissions(String roleAlias) {
        List<RlRolePermission> rls = rolePermissionMapper.selectList(Wrappers
                .lambdaQuery(RlRolePermission.class).eq(RlRolePermission::getRoleAlias, roleAlias));
        return rls.stream().map(RlRolePermission::getPermissionAlias).collect(Collectors.toList());
    }

    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MERCHANT_ROLE_CACHE_NAME,
            key = "'" + CacheConstants.MERCHANT_ROLE_CACHE_KEY_PREFIX + "' + #root.args[0]")
    @Override
    public String findMerchantRole(Integer merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        return merchant == null ? null : merchant.getRoleAlias();
    }

    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MERCHANT_PERMISSIONS_CACHE_NAME,
            key = "'" + CacheConstants.MERCHANT_PERMISSIONS_CACHE_KEY_PREFIX + "' + #root.args[0]")
    @Override
    public List<String> queryMerchantPermissions(Integer merchantId) {
        String merchantRole = findMerchantRole(merchantId);
        if (merchantRole == null) {
            return new ArrayList<>(0);
        }
        return queryRoleAssociationPermissions(merchantRole);
    }

}
