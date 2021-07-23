package com.yfshop.admin.api.spread;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.spread.request.SpreadItemReq;
import com.yfshop.admin.api.spread.request.SpreadOrderReq;
import com.yfshop.admin.api.spread.request.SpreadWithdrawReq;
import com.yfshop.admin.api.spread.result.*;
import com.yfshop.common.exception.ApiException;

import java.util.List;

public interface AdminSpreadService {

    IPage<SpreadItemResult> getItemList(SpreadItemReq spreadItemReq) throws ApiException;

    Void createItem(SpreadItemReq spreadItemReq) throws ApiException;

    IPage<SpreadOrderResult> getOrderList(SpreadOrderReq spreadOrderReq) throws ApiException;

    List<SpreadOrderExport> getOrderExport(SpreadOrderReq spreadOrderReq) throws ApiException;

    IPage<SpreadWithdrawResult> getWithdrawList(SpreadWithdrawReq spreadWithdrawReq) throws ApiException;

    Void tryWithdraw(Long id) throws ApiException;

    List<SpreadWithdrawExport> getWithdrawExport(SpreadWithdrawReq spreadWithdrawReq);

    SpreadStatsResult getSpreadStats() throws ApiException;
}
