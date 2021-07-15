package com.yfshop.admin.dao;

import com.yfshop.admin.api.push.request.WxPushTaskReq;
import com.yfshop.admin.api.push.result.WxPushTaskData;
import com.yfshop.code.model.UserCoupon;

import java.util.List;

public interface WxPushTaskDao {

    List<WxPushTaskData> getWxPushTaskData(WxPushTaskReq req);

    Integer getWxPushTaskDataCount(WxPushTaskReq req);
}
