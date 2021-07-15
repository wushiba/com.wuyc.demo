package com.yfshop.admin.api.push;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.push.request.WxPushTaskReq;
import com.yfshop.admin.api.push.result.WxPushFailExportResult;
import com.yfshop.admin.api.push.result.WxPushTaskResult;
import com.yfshop.admin.api.push.result.WxPushTaskStatsResult;
import com.yfshop.common.exception.ApiException;

import java.util.List;

public interface WxPushTaskService {

    void createPushTask(WxPushTaskReq wxPushTaskReq) throws ApiException;

    void closePushTask(Integer id) throws ApiException;

    void editPushTask(WxPushTaskReq wxPushTaskReq) throws ApiException;

    Integer filterPushData(WxPushTaskReq wxPushTaskReq) throws ApiException;

    IPage<WxPushTaskResult> pushTaskList(WxPushTaskReq wxPushTaskReq);

    WxPushTaskStatsResult pushTaskStats();

    List<WxPushFailExportResult> pushFailExport(Integer id) throws ApiException;


}
