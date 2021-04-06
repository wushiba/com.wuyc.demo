package com.yfshop.admin.controller;

import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import com.yfshop.common.base.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractBaseController implements BaseController {

    @Autowired
    SaTokenDaoRedisJackson saTokenDaoRedis;

    @Override
    public String getCurrentOpenId() {
        String openId = getCookieValue("yfopen");
        if (StringUtils.isNotBlank(openId)) {
            return saTokenDaoRedis.get(String.format("yfopen:login:token:%s", openId));
        }
        return null;
    }

    @Override
    public Integer getCurrentUserId() {
        String userId = getCookieValue("user");
        if (StringUtils.isNotBlank(userId)) {
            String u = saTokenDaoRedis.get(String.format("user:login:token:%s", userId));
            return u == null ? null : Integer.valueOf(u);
        }
        return null;
    }

}
