package com.yfshop.admin.api.healthy;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.healthy.request.HealthyActReq;
import com.yfshop.admin.api.healthy.request.QueryHealthyOrderReq;
import com.yfshop.admin.api.healthy.request.QueryHealthySubOrderReq;
import com.yfshop.admin.api.healthy.result.HealthyOrderDetailResult;
import com.yfshop.admin.api.healthy.result.HealthyOrderResult;
import com.yfshop.admin.api.healthy.result.HealthySubOrderResult;
import com.yfshop.common.exception.ApiException;

public interface AdminHealthyService {

    IPage<HealthyOrderResult> findOrderList(QueryHealthyOrderReq req);


    HealthyOrderDetailResult getOrderDetail(Integer id);

    IPage<HealthySubOrderResult> findSubOrderList(QueryHealthySubOrderReq req);
   
    Void addAct(HealthyActReq req);
    Void notifyByWechatPay(String orderNo, String wechatBillNo) throws ApiException;
}
