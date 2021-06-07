package com.yfshop.admin.api.express;

import com.yfshop.admin.api.express.result.ExpressOrderResult;
import com.yfshop.admin.api.express.result.ExpressResult;
import com.yfshop.common.exception.ApiException;

import java.util.List;

public interface ExpressService {

    ExpressOrderResult queryExpress(Long id) throws ApiException;

    List<ExpressResult> queryByExpressNo(String expressNo,String expressName,String receiverMobile) throws ApiException;
}
