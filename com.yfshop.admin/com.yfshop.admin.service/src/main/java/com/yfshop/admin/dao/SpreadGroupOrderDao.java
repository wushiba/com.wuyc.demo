package com.yfshop.admin.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.spread.request.SpreadGroupOrderReq;
import com.yfshop.admin.api.spread.result.SpreadGroupOrderResult;
import com.yfshop.admin.api.spread.result.SpreadGroupOrderStatsResult;
import com.yfshop.code.model.Order;
import org.apache.ibatis.annotations.Param;

/**
 * @author wuyc
 * created 2021/4/7 14:36
 **/
public interface SpreadGroupOrderDao {

    IPage<SpreadGroupOrderResult> getGroupOrderList(IPage page, @Param("req") SpreadGroupOrderReq groupOrderReq);

    SpreadGroupOrderStatsResult getGroupOrderStats(SpreadGroupOrderReq groupOrderReq);

    SpreadGroupOrderStatsResult getOrderStats(SpreadGroupOrderReq groupOrderReq);
}
