package com.yfshop.admin.dao;

import com.yfshop.admin.api.draw.request.QueryDrawRecordSatsReq;
import com.yfshop.admin.api.draw.result.*;
import com.yfshop.code.model.TraceDraw;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DrawRecordDao {

    List<DrawRecordSatsByDayResult> satsByDay(@Param("req") QueryDrawRecordSatsReq req);

    List<DrawRecordSatsByLevelResult> satsByLeve(@Param("req") QueryDrawRecordSatsReq req);

    List<DrawRecordSatsByProvinceResult> satsByProvince(@Param("req") QueryDrawRecordSatsReq req);

    List<DrawRecordSatsByJxsResult> satsByJxs(@Param("req") QueryDrawRecordSatsReq recordReq);

    void updateTrace(@Param("id") Integer id,@Param("updateTime")String updateTime,@Param("dealerAddress")String dealerAddress,@Param("dealerName")String dealerName);

    List<TraceDrawResult> getTractNo(Long id);
}
