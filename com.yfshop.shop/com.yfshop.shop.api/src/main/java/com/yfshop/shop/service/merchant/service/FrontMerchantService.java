package com.yfshop.shop.service.merchant.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.merchant.req.QueryMerchant;
import com.yfshop.shop.service.merchant.result.MerchantResult;
import com.yfshop.shop.service.merchant.result.WebsiteCodeDetailResult;

import java.util.List;

public interface FrontMerchantService {

    /**
     * 根据当前位置查询附近门店
     *
     * @param districtId 区id
     * @param longitude  经度
     * @param latitude   纬度
     * @return
     * @throws ApiException
     */
    List<MerchantResult> findNearMerchantList(Integer districtId, Double longitude, Double latitude) throws ApiException;

    /**
     * 根据网点码查询商户信息
     *
     * @param websiteCode 网点码
     * @return
     * @throws ApiException
     */
    MerchantResult getMerchantByWebsiteCode(String websiteCode) throws ApiException;

    /**
     * 用户自提二等奖成功后，生成网点记账单
     *
     * @param orderId 用户主订单id
     * @return
     * @throws ApiException
     */
    Void insertWebsiteBill(Long orderId) throws ApiException;


    IPage<MerchantResult> findMerchantList(QueryMerchant queryMerchant) throws ApiException;


}
