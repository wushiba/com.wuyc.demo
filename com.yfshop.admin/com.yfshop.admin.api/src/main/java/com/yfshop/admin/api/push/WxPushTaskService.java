package com.yfshop.admin.api.push;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.push.request.WxPushTaskReq;
import com.yfshop.admin.api.push.result.WxPushFailExportResult;
import com.yfshop.admin.api.push.result.WxPushTaskResult;
import com.yfshop.admin.api.push.result.WxPushTaskStatsResult;
import com.yfshop.admin.api.push.result.WxPushTemplateResult;
import com.yfshop.common.exception.ApiException;

import java.util.List;

public interface WxPushTaskService {

    Void createPushTask(WxPushTaskReq wxPushTaskReq) throws ApiException;

    Void closePushTask(Integer id) throws ApiException;

    Void editPushTask(WxPushTaskReq wxPushTaskReq) throws ApiException;

    String downloadFile(Integer id) throws ApiException;

    Integer filterPushData(WxPushTaskReq wxPushTaskReq) throws ApiException;

    IPage<WxPushTaskResult> pushTaskList(WxPushTaskReq wxPushTaskReq);

    WxPushTaskStatsResult pushTaskStats();

    List<WxPushFailExportResult> pushFailExport(Integer id) throws ApiException;

    List<WxPushTemplateResult> pushTemplateList() throws ApiException;
}
