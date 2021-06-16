package com.yfshop.admin.api.draw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.draw.request.QueryDrawRecordReq;
import com.yfshop.admin.api.draw.request.QueryDrawRecordSatsReq;
import com.yfshop.admin.api.draw.result.*;

import java.util.List;

/**
 * @Title:抽奖记录接口
 * @Description:
 * @Since:2021-03-24 11:13:23
 * @Version:1.1.0
 */
public interface AdminDrawRecordService {

    IPage<DrawRecordResult> getDrawRecordList(QueryDrawRecordReq queryDrawRecordReq);

    List<DrawRecordSatsByDayResult> satsByDay(QueryDrawRecordSatsReq recordReq);

    List<DrawRecordSatsByLevelResult> satsByLeve(QueryDrawRecordSatsReq recordReq);

    List<DrawRecordSatsByProvinceResult> satsByProvince(QueryDrawRecordSatsReq recordReq);

    List<DrawRecordSatsByJxsResult> satsByJxs(QueryDrawRecordSatsReq recordReq);
}
