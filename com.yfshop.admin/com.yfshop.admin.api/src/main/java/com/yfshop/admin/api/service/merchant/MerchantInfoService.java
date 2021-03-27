package com.yfshop.admin.api.service.merchant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.service.merchant.result.MerchantResult;
import com.yfshop.admin.api.website.req.WebsiteCodeAddressReq;
import com.yfshop.admin.api.website.req.WebsiteCodeBindReq;
import com.yfshop.admin.api.website.req.WebsiteCodePayReq;
import com.yfshop.admin.api.website.result.*;
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
    MerchantResult websiteCodeBind(WebsiteCodeBindReq websiteReq) throws ApiException;


    /**
     * 我申请的网点码
     *
     * @param merchantId
     * @param status
     * @param dateTime
     * @return
     * @throws ApiException
     */
    List<WebsiteCodeDetailResult> getMyWebsiteCode(Integer merchantId, String status, Date dateTime) throws ApiException;

    /**
     * 获取待申请网点码
     *
     * @param merchantId
     * @param status
     * @param pageIndex
     * @param pageSize
     * @return
     */
    IPage<WebsiteCodeResult> applyWebsiteCodeStatus(Integer merchantId, String status, Integer pageIndex, Integer pageSize) throws ApiException;

    /**
     * 更新待申请网点码
     *
     * @param id
     * @param status
     * @throws ApiException
     */
    Void updateApplyWebsiteCode(Integer id, String status) throws ApiException;

    /**
     * 申请网点码
     *
     * @param merchantId
     * @param count
     * @param email
     * @throws ApiException
     */
    Integer applyWebsiteCode(Integer merchantId, Integer count, String email) throws ApiException;

    /**
     * 创建更新网点码收货地址
     *
     * @param websiteCodeAddressReq
     * @return
     * @throws ApiException
     */
    Void websiteCodeAddress(WebsiteCodeAddressReq websiteCodeAddressReq) throws ApiException;

    /**
     * 获取网点码收货地址
     *
     * @param currentAdminUserId
     * @return
     * @throws ApiException
     */
    List<WebsiteCodeAddressResult> getWebsiteCodeAddress(Integer currentAdminUserId) throws ApiException;

    /**
     * 删除网点码收货地址
     *
     * @param id
     * @return
     */
    Void deleteWebsiteCodeAddress(Integer id) throws ApiException;

    /**
     * 获取网点码价格
     *
     * @param ids
     * @return
     */
    WebsiteCodeAmountResult applyWebsiteCodeAmount(List<Integer> ids) throws ApiException;

    /**
     * 获取网点码订单详情
     *
     * @param id
     * @return
     */
    WebsiteCodeResult applyWebsiteCodeDetails(Integer id) throws ApiException;

    /**
     * 网点码支付
     *
     * @param websiteCodePayReq
     * @return
     */
    WebsiteCodePayResult applyWebsiteCodePay(WebsiteCodePayReq websiteCodePayReq) throws ApiException;

    Integer checkWebsiteCode(String websiteCode) throws ApiException;

    /**
     * 获取网点信息
     *
     * @param websiteCode
     * @return
     */
    MerchantResult getMerchantByWebsiteCode(String websiteCode) throws ApiException;

    MerchantResult getMerchantByOpenId(String openId) throws ApiException;
}
