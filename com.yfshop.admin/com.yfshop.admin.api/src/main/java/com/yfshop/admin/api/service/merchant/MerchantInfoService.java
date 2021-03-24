package com.yfshop.admin.api.service.merchant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.service.merchant.result.MerchantResult;
import com.yfshop.admin.api.website.req.WebsiteCodeBindReq;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailResult;
import com.yfshop.admin.api.website.result.WebsiteCodeResult;
import com.yfshop.admin.api.website.result.WebsiteTypeResult;
import com.yfshop.common.exception.ApiException;

import java.util.Date;
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

    /**
     * 获取网点类型
     *
     * @return
     */
    List<WebsiteTypeResult> getWebsiteType() throws ApiException;

    /**
     * 判定商户码
     *
     * @param websiteReq
     * @return
     */
    Void websiteCodeBind(WebsiteCodeBindReq websiteReq) throws ApiException;


    /**
     * 我申请的网点码
     * @param merchantId
     * @param status
     * @param dateTime
     * @return
     * @throws ApiException
     */
    List<WebsiteCodeDetailResult> getMyWebsiteCode(Integer merchantId, String status, Date dateTime) throws ApiException;

    /**
     * 获取待申请网点码
     * @param merchantId
     * @param status
     * @param pageIndex
     * @param pageSize
     * @return
     */
    IPage<WebsiteCodeResult> getApplyWebsiteCode(Integer merchantId, String status, Integer pageIndex, Integer pageSize);
}
