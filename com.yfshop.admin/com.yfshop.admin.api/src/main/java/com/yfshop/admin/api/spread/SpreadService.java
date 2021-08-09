package com.yfshop.admin.api.spread;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.spread.request.SpreadBillReq;
import com.yfshop.admin.api.spread.request.SpreadItemReq;
import com.yfshop.admin.api.spread.request.SpreadOrderReq;
import com.yfshop.admin.api.spread.request.SpreadWithdrawReq;
import com.yfshop.admin.api.spread.result.*;
import com.yfshop.common.exception.ApiException;

import java.math.BigDecimal;
import java.util.List;

public interface SpreadService {

    String createPromotion(Integer merchantId, Integer itemId) throws Exception;

    List<SpreadItemResult> getItemList() throws ApiException;

    SpreadItemResult getItemDetail(Integer id) throws ApiException;

    SpreadStatsResult getSpreadStats(Integer merchantId) throws ApiException;

    IPage<SpreadOrderResult> getOrderList(SpreadOrderReq spreadOrderReq) throws ApiException;

    SpreadOrderResult getOrderDetail(Long id) throws ApiException;

    BigDecimal getBalance(Integer merchantId) throws ApiException;

    IPage<SpreadBillResult> getBillList(SpreadBillReq spreadBillReq) throws ApiException;

    Void withDraw(SpreadWithdrawReq spreadWithdrawReq) throws ApiException;

    String getLongUrlByShortCode(String shortCode) throws ApiException;
}
