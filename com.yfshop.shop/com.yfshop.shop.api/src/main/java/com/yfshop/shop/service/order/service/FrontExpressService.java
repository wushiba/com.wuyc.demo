package com.yfshop.shop.service.order.service;

import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.express.result.ExpressResult;

import java.util.List;

public interface FrontExpressService {

    List<ExpressResult> queryExpress(Long id) throws ApiException;

    List<ExpressResult> queryExpressByWayBillNo(String id) throws ApiException;
}