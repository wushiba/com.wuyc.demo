package com.yfshop.admin.api.express;

import com.yfshop.admin.api.express.result.ExpressResult;
import com.yfshop.common.exception.ApiException;

import java.util.List;

public interface ExpressService {

    List<ExpressResult> queryExpress(Long id) throws ApiException;

    List<ExpressResult> queryExpressByWayBillNo(String id) throws ApiException;
}
