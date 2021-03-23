package com.yfshop.admin.api.rolepermission;

import com.yfshop.admin.api.rolepermission.request.AssociateRolePermissionReq;
import com.yfshop.admin.api.rolepermission.request.CreatePermissionReq;
import com.yfshop.admin.api.rolepermission.result.RoleResult;
import com.yfshop.common.exception.ApiException;

import java.util.List;

/**
 * 角色权限管理
 *
 * @author Xulg
 * Created in 2021-03-23 14:50
 */
public interface RolePermissionManageService {

    /**
     * 创建权限
     *
     * @param req        the req
     * @param merchantId the user id
     * @return void
     * @throws ApiException e
     */
    Void createPermission(Integer merchantId, CreatePermissionReq req) throws ApiException;

    /**
     * 关联角色和权限
     *
     * @param req the req
     * @return void
     * @throws ApiException e
     */
    Void associateRolePermission(AssociateRolePermissionReq req) throws ApiException;

    /**
     * 查询所有的角色
     *
     * @return the role list
     */
    List<RoleResult> queryRoles();

    /**
     * 查询角色关联的权限列表
     *
     * @param roleAlias the role alias
     * @return the permission list
     */
    List<String> queryRoleAssociationPermissions(String roleAlias);

    /**
     * 查询商户的角色
     *
     * @param merchantId the merchant id
     * @return the role alias list
     */
    String findMerchantRole(Integer merchantId);

    /**
     * 查询商户的权限
     *
     * @param merchantId the merchant id
     * @return the permission alias list
     */
    List<String> queryMerchantPermissions(Integer merchantId);
}
