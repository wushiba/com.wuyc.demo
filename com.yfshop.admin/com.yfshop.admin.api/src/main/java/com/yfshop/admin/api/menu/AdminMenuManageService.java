package com.yfshop.admin.api.menu;

import com.yfshop.admin.api.menu.result.MenuResult;

import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-23 16:43
 */
public interface AdminMenuManageService {

    /**
     * 查询商户的菜单
     *
     * @param merchantId the merchant id
     * @return the menu list
     */
    List<MenuResult> queryMerchantMenus(Integer merchantId);

    /**
     * 根据角色查询菜单
     *
     * @param roleAlias the role alias
     * @return the menu list
     */
    List<MenuResult> queryMenusByRoleAlias(String roleAlias);
}
