package com.yfshop.admin.api.website;

import com.yfshop.admin.api.website.result.WebsiteBillDayResult;
import com.yfshop.common.exception.ApiException;
import java.util.Date;
import java.util.List;

/**
 * 网店记账服务
 *
 * @author youshenghui
 * Created in 2021-03-23 9:10
 */
public interface WebsiteBillService {

    /**
     * 获取网点记账列表
     *
     * @param merchantId
     * @param dateTime
     * @param status
     * @return
     */
    WebsiteBillDayResult getBillListByMerchantId(Integer merchantId, Date startTime,Date endTime, String status) throws ApiException;

    /**
     * 获取网点记账列表
     *
     * @param websiteCode
     * @param dateTime
     * @return
     */
    WebsiteBillDayResult getBillByWebsiteCode(String websiteCode, Date dateTime);

    /**
     * 账单确认
     *
     * @param merchantId
     * @param billIds
     * @return
     */
    Void billConfirm(Integer merchantId, List<Long> billIds) throws ApiException;

    /**
     * 一键确认
     *
     * @param merchantId
     * @return
     */
    Void billAllConfirm(Integer merchantId) throws ApiException;

    /**
     * 用户自提二等奖成功后，生成网点记账单
     * @param orderId     用户主订单id
     * @return Void
     * @throws ApiException
     */
    Void insertWebsiteBill(Long orderId) throws ApiException;

}
