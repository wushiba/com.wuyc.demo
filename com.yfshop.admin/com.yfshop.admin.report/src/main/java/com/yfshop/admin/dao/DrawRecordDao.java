package com.yfshop.admin.dao;

import com.yfshop.admin.api.draw.request.QueryDrawRecordSatsReq;
import com.yfshop.admin.api.draw.result.DrawRecordSatsByDayResult;
import com.yfshop.admin.api.draw.result.DrawRecordSatsByJxsResult;
import com.yfshop.admin.api.draw.result.DrawRecordSatsByLevelResult;
import com.yfshop.admin.api.draw.result.DrawRecordSatsByProvinceResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DrawRecordDao {

    List<DrawRecordSatsByDayResult> satsByDay(@Param("req") QueryDrawRecordSatsReq req);

    List<DrawRecordSatsByLevelResult> satsByLeve(@Param("req") QueryDrawRecordSatsReq req);

    List<DrawRecordSatsByProvinceResult> satsByProvince(@Param("req") QueryDrawRecordSatsReq req);

    List<DrawRecordSatsByJxsResult> satsByJxs(@Param("req") QueryDrawRecordSatsReq recordReq);
}
