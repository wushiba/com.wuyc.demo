package com.yfshop.admin.api.order.service;

import com.yfshop.admin.api.order.request.QueryOrderReq;
import com.yfshop.admin.api.order.result.OrderExportResult;
import com.yfshop.common.exception.ApiException;

import java.util.List;

/**
 * @Title:用户订单Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-31 16:09:37
 * @Version:1.1.0
 */
public interface AdminUserOrderExportService {

    /**
     * 查询订列表
     *
     * @param req
     * @return
     */
    List<OrderExportResult> orderExport(QueryOrderReq req) throws ApiException;

}
