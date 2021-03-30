package com.yfshop.admin.api.merchant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.merchant.request.CreateMerchantReq;
import com.yfshop.admin.api.merchant.request.QueryMerchantReq;
import com.yfshop.admin.api.merchant.request.UpdateMerchantReq;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.common.exception.ApiException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Xulg
 * Created in 2021-03-25 11:20
 */
public interface AdminMerchantManageService {

    /**
     * 创建商户
     *
     * @param req the req
     * @return void
     * @throws ApiException e
     */
    Void createMerchant(@Valid @NotNull CreateMerchantReq req) throws ApiException;

    /**
     * 编辑商户
     *
     * @param req the req
     * @return void
     * @throws ApiException e
     */
    Void updateMerchant(@Valid @NotNull UpdateMerchantReq req) throws ApiException;

    /**
     * 分页查询商户列表
     *
     * @param req the req
     * @return the page data
     */
    IPage<MerchantResult> pageQueryMerchants(QueryMerchantReq req);

    /**
     * 启用|禁用商户
     *
     * @param merchantId the merchant id
     * @param isEnable   is enable
     * @return void
     * @throws ApiException e
     */
    Void updateMerchantIsEnable(@NotNull(message = "商户ID不能为空") Integer merchantId, boolean isEnable) throws ApiException;
}
