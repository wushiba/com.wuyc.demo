package com.yfshop.admin.api.service.merchant;

import com.yfshop.admin.api.service.merchant.result.MerchantResult;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailResult;
import com.yfshop.common.exception.ApiException;

import java.util.List;

public interface MerchantInfoService {

    /**
     * 获取网点信息
     *
     * @param merchantId
     * @return
     */
    MerchantResult getWebsiteInfo(Integer merchantId) throws ApiException;


    /**
     * 获取网点商户码信息
     *
     * @param merchantId
     * @return
     * @throws ApiException
     */
    List<WebsiteCodeDetailResult> getWebsiteCode(Integer merchantId) throws ApiException;
}
