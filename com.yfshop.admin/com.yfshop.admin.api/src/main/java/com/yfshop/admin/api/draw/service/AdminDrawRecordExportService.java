package com.yfshop.admin.api.draw.service;

import com.yfshop.admin.api.draw.request.QueryDrawRecordExportReq;
import com.yfshop.admin.api.draw.result.DrawRecordExportResult;

import java.util.List;

/**
 * @Title:抽奖记录接口
 * @Description:
 * @Since:2021-03-24 11:13:23
 * @Version:1.1.0
 */
public interface AdminDrawRecordExportService {

    List<DrawRecordExportResult> getDrawRecordExport(QueryDrawRecordExportReq queryDrawRecordExportReq);
}
