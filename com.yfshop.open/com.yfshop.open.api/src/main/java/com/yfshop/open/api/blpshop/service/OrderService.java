package com.yfshop.open.api.blpshop.service;

import com.yfshop.common.exception.ApiException;
import com.yfshop.open.api.blpshop.request.*;
import com.yfshop.open.api.blpshop.result.*;

public interface OrderService {

    OrderResult getOrder(OrderReq orderReq) throws ApiException;

    SendResult send(SendReq sendReq) throws ApiException;

    CheckRefundStatusResult checkRefundStatus(CheckRefundStatusReq checkRefundStatusReq) throws ApiException;

    DownloadProductResult downloadProduct(DownloadProductReq downloadProductReq) throws ApiException;

    SyncStockResult syncStock(SyncStockReq syncStockReq) throws ApiException;

    RefundResult getRefund(RefundReq refundReq) throws ApiException;

}
