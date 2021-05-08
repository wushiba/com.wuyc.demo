package com.yfshop.shop.service.activity.service;

import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.activity.result.YfActCodeBatchDetailResult;
import com.yfshop.shop.service.activity.result.YfDrawActivityResult;
import com.yfshop.shop.service.activity.result.YfDrawPrizeResult;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;

/**
 * @Title:活动抽奖Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 19:09:17
 * @Version:1.1.0
 */
public interface FrontDrawRecordService {

    /**
     * 保存抽奖记录
     *
     * @param userId
     * @param drawPrize
     * @param location
     */
    void saveDrawRecord(Integer userId, YfActCodeBatchDetailResult actCodeBatchDetailResult, YfDrawPrizeResult drawPrize, String location);

    /**
     * 更新抽奖记录状态
     * @param actCode
     * @param useStatus
     * @param userName
     * @param userMobile
     */
    void updateDrawRecord(String actCode, String useStatus, String userName, String userMobile);


    /**
     * 更新抽奖记录状态
     * @param useStatus
     */
    void updateDrawRecordUseStatus(Long userCoupId, String useStatus);

}
