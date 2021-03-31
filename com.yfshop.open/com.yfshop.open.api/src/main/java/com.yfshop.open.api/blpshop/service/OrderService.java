package com.yfshop.open.api.blpshop.service;

import com.yfshop.common.exception.ApiException;
import com.yfshop.open.api.blpshop.request.OrderReq;
import com.yfshop.open.api.blpshop.result.OrderResult;

public interface OrderService {

    OrderResult getOrder(OrderReq orderReq) throws ApiException;
}
