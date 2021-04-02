package com.yfshop.shop.service.merchant.service;

import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.merchant.result.MerchantResult;
import com.yfshop.shop.service.merchant.result.WebsiteCodeDetailResult;
import java.util.List;

public interface FrontMerchantService {

    /**
     * 根据当前位置查询附近门店
     * @param districtId    区id
     * @param longitude     经度
     * @param latitude      纬度
     * @return
     * @throws ApiException
     */
    List<MerchantResult> findNearMerchantList(Integer districtId, Double longitude, Double latitude) throws ApiException;

    /**
     * 根据网点码查询商户信息
     * @param websiteCode   网点码
     * @return
     * @throws ApiException
     */
    WebsiteCodeDetailResult getWebsiteCodeDetailByWebsiteCode(String websiteCode) throws ApiException;

}
