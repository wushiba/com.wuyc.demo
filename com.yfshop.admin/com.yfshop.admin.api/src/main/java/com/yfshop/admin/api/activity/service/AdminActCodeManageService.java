package com.yfshop.admin.api.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.activity.request.ActCodeQueryDetailsReq;
import com.yfshop.admin.api.activity.request.ActCodeQueryReq;
import com.yfshop.admin.api.activity.result.ActCodeBatchRecordResult;
import com.yfshop.admin.api.activity.result.ActCodeResult;
import com.yfshop.admin.api.activity.result.ActCodeDetailsResult;
import com.yfshop.common.exception.ApiException;

import java.util.List;

public interface AdminActCodeManageService {

    IPage<ActCodeResult> queryActCodeList(ActCodeQueryReq actCodeQueryReq) throws ApiException;

    Void actCodeImport(Integer actId, String md5, String fileUrl) throws ApiException;

    Void checkFile(String md5) throws ApiException;

    String actCodeUrl(Integer merchantId,Integer id);

    Void sendEmailActCode(Integer currentAdminUserId, Integer id, Integer factoryId);

    IPage<ActCodeDetailsResult> queryActCodeDetails(ActCodeQueryDetailsReq actCodeQueryReq);

    List<ActCodeBatchRecordResult> queryActCodeDownloadRecord(Integer batchId);
}
