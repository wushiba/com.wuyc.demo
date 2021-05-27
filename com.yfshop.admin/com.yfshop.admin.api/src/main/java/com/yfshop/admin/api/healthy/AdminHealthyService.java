package com.yfshop.admin.api.healthy;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.healthy.request.HealthyActReq;
import com.yfshop.admin.api.healthy.request.HealthyItemReq;
import com.yfshop.admin.api.healthy.request.QueryHealthyOrderReq;
import com.yfshop.admin.api.healthy.request.QueryHealthySubOrderReq;
import com.yfshop.admin.api.healthy.result.*;
import com.yfshop.common.exception.ApiException;

import java.util.List;

public interface AdminHealthyService {

    IPage<HealthyOrderResult> findOrderList(QueryHealthyOrderReq req);


    HealthyOrderDetailResult getOrderDetail(Integer id);

    IPage<HealthySubOrderResult> findSubOrderList(QueryHealthySubOrderReq req);

    Void addAct(HealthyActReq req);

    IPage<HealthyActResult> getActList(HealthyActReq req);

    Void notifyByWechatPay(String orderNo, String wechatBillNo) throws ApiException;

    Void updateAct(HealthyActReq req);

    Void addItem(HealthyItemReq req);

    Void updateItem(HealthyItemReq req);
    IPage<HealthyItemResult> getItemList(HealthyItemReq req);
}
