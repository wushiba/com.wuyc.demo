package com.yfshop.admin.api.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.activity.request.ActCodeQueryReq;
import com.yfshop.admin.api.activity.result.ActCodeResult;

public interface AdminActCodeManageService {

    IPage<ActCodeResult> queryActCodeList(ActCodeQueryReq actCodeQueryReq);
}
