package com.yfshop.admin.api.merchant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.merchant.request.CreateMerchantReq;
import com.yfshop.admin.api.merchant.request.QueryMerchantReq;
import com.yfshop.admin.api.merchant.request.UpdateMerchantReq;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.common.exception.ApiException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-25 11:20
 */
public interface AdminMerchantManageService {

    /**
     * 创建商户
     *
     * @param merchantId the merchant id
     * @param req        the req
     * @return void
     * @throws ApiException e
     */
    Void createMerchant(@NotNull Integer merchantId, @Valid @NotNull CreateMerchantReq req) throws ApiException;

    /**
     * 编辑商户
     *
     * @param merchantId the merchant id
     * @param req        the req
     * @return void
     * @throws ApiException e
     */
    Void updateMerchant(@NotNull Integer merchantId, @Valid @NotNull UpdateMerchantReq req) throws ApiException;

    /**
     * 分页查询商户列表
     *
     * @param merchantId the merchant id
     * @param req        the req
     * @return the page data
     */
    IPage<MerchantResult> pageQueryMerchants(Integer merchantId, QueryMerchantReq req);

    /**
     * 启用|禁用商户
     *
     * @param merchantId the merchant id
     * @param isEnable   is enable
     * @return void
     * @throws ApiException e
     */
    Void updateMerchantIsEnable(@NotNull(message = "商户ID不能为空") Integer merchantId, boolean isEnable) throws ApiException;

    /**
     * 根据角色标识查询商户列表
     *
     * @param merchantId   the merchant id
     * @param roleAlias    the role alias
     * @param merchantName the merchant name
     * @param pageIndex    the page index
     * @param pageSize     the page size
     * @return the merchant list
     */
    IPage<MerchantResult> pageQueryMerchantsByPidAndRoleAlias(Integer merchantId, String roleAlias, String merchantName,
                                                              Integer pageIndex, Integer pageSize);
}
