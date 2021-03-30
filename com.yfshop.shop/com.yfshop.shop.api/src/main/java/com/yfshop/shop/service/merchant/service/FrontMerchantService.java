package com.yfshop.shop.service.merchant.service;

import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.merchant.result.MerchantResult;
import java.util.List;

public interface FrontMerchantService {

    /**
     * 根据当前位置查询附近门店
     * @param longitude     经度
     * @param latitude      纬度
     * @return
     * @throws ApiException
     */
    List<MerchantResult> findNearMerchantList(Double longitude, Double latitude) throws ApiException;

}
