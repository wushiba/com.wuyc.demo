package com.yfshop.admin.api.healthy;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.healthy.request.HealthyActReq;
import com.yfshop.admin.api.healthy.request.QueryHealthyOrderReq;
import com.yfshop.admin.api.healthy.request.QueryHealthySubOrderReq;
import com.yfshop.admin.api.healthy.result.HealthyOrderDetailResult;
import com.yfshop.admin.api.healthy.result.HealthyOrderResult;
import com.yfshop.admin.api.healthy.result.HealthySubOrderResult;

public interface AdminHealthyService {

    IPage<HealthyOrderResult> findOrderList(QueryHealthyOrderReq req);


    HealthyOrderDetailResult getOrderDetail(Integer id);

    IPage<HealthySubOrderResult> findSubOrderList(QueryHealthySubOrderReq req);

    Void addAct(HealthyActReq req);
}
