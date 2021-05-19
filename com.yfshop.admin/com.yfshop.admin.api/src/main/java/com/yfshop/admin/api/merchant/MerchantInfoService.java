package com.yfshop.admin.api.merchant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.yfshop.admin.api.merchant.request.MerchantGroupReq;
import com.yfshop.admin.api.merchant.request.MerchantReq;
import com.yfshop.admin.api.merchant.request.QueryGoodsRecordReq;
import com.yfshop.admin.api.merchant.result.GoodsRecordResult;
import com.yfshop.admin.api.merchant.result.MerchantGroupResult;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.admin.api.website.request.WebsiteCodeAddressReq;
import com.yfshop.admin.api.website.request.WebsiteCodeBindReq;
import com.yfshop.admin.api.website.request.WebsiteCodeDataReq;
import com.yfshop.admin.api.website.request.WebsiteCodePayReq;
import com.yfshop.admin.api.website.result.*;
import com.yfshop.common.exception.ApiException;
import com.yfshop.wx.api.request.WxPayOrderNotifyReq;

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
     * 判定商户码
     *
     * @param websiteReq
     * @return
     */
    MerchantResult websiteCodeBind(WebsiteCodeBindReq websiteReq) throws ApiException;


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
     * 我申请的网点码
     *
     * @param merchantId
     * @param status
     * @return
     * @throws ApiException
     */
    List<WebsiteCodeDetailResult> getMyWebsiteCode(Integer merchantId, String status, Date startTime, Date sendTime) throws ApiException;

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
    WxPayMpOrderResult applyWebsiteCodePay(WebsiteCodePayReq websiteCodePayReq) throws ApiException;

    /**
     * 校验网点码跳转
     *
     * @param websiteCode
     * @return
     * @throws ApiException
     */
    Integer checkWebsiteCode(String websiteCode) throws ApiException;

    /**
     * 获取网点信息
     *
     * @param websiteCode
     * @return
     */
    MerchantResult getMerchantByWebsiteCode(String websiteCode) throws ApiException;

    /**
     * 获取网点码
     *
     * @param openId
     * @return
     * @throws ApiException
     */
    MerchantResult getMerchantByOpenId(String openId) throws ApiException;

    /**
     * 网点码支付回调
     *
     * @param notifyResult
     */
    void websitePayOrderNotify(WxPayOrderNotifyReq notifyResult) throws ApiException;

    /**
     * 获取商户层级数据
     *
     * @param merchantGroupReq
     * @return
     */
    MerchantGroupResult merchantGroup(MerchantGroupReq merchantGroupReq) throws ApiException;

    Void save(MerchantReq merchantReq) throws ApiException;

    List<MerchantResult> getChildMerchant(Integer merchantId) throws ApiException;

    Integer checkActivate(String websiteCode);

    void cancelWebsiteCodePay(WebsiteCodePayReq websiteCodePayReq);

    MerchantGroupResult getWebsiteList(MerchantGroupReq merchantGroupReq);

    WebsiteCodeGroupResult getWebsiteCodeData(Integer merchantId,WebsiteCodeDataReq websiteCodeDataReq);

    List<MerchantResult> findNearMerchantList(Integer merchantId,Integer districtId, Double longitude, Double latitude);

    Void websiteAddGoods(Integer merchantId, String mobile, Integer count) throws ApiException;

    MerchantResult getWebsiteByMobile(String mobile) throws ApiException;

    List<GoodsRecordResult> websiteGoodsList(QueryGoodsRecordReq merchantId);

    Integer getWebsiteCodeBindCount(Integer merchantId);

    void updateOpenId(Integer merchantId,String openId);
}
