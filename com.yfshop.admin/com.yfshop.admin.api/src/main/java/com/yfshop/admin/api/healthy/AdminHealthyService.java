package com.yfshop.admin.api.healthy;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.healthy.request.*;
import com.yfshop.admin.api.healthy.result.*;
import com.yfshop.common.exception.ApiException;

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

    IPage<JxsMerchantResult> findJxsMerchant(QueryJxsMerchantReq req);

    Void updateSubOrderPostWay(SubOrderPostWay req);

    HealthyItemResult getItemDetail(Integer id);
}
