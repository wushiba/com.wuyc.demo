package com.yfshop.shop.service.order.service;

import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.express.result.ExpressOrderResult;
import com.yfshop.shop.service.express.result.ExpressResult;

import java.util.List;

public interface FrontExpressService {

    ExpressOrderResult queryExpress(Long id) throws ApiException;

    ExpressOrderResult queryByExpressNo(String expressNo,String expressName,String receiverMobile) throws ApiException;
}