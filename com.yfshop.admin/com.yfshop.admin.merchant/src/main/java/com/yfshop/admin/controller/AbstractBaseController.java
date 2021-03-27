package com.yfshop.admin.controller;

import com.yfshop.admin.config.SaTokenDaoRedis;
import com.yfshop.common.base.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractBaseController implements BaseController {

    @Autowired
    SaTokenDaoRedis saTokenDaoRedis;

    @Override
    public String getCurrentOpenId() {
        String openId = getCookieValue("yfopen");
        if (StringUtils.isNotBlank(openId)) {
            return saTokenDaoRedis.get(String.format("yfopen:wx:token:%s", openId));
        }
        return null;
    }

    @Override
    public Integer getCurrentUserId() {
        String userId = getCookieValue("shop");
        if (StringUtils.isNotBlank(userId)) {
            String u = saTokenDaoRedis.get(String.format("shop:user:token:%s", userId));
            return u == null ? null : Integer.valueOf(u);
        }
        return null;
    }

}
