package com.yfshop.admin.controller;

import com.yfshop.auth.api.service.AuthService;
import com.yfshop.common.base.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;

public abstract class AbstractBaseController implements BaseController {

    @DubboReference
    public AuthService authService;

    @Override
    public String getCurrentOpenId() {
        String openId = getCookieValue("yfopen");
        if (StringUtils.isNotBlank(openId)) {
            return authService.get(String.format("yfopen:login:token:%s", openId));
        }
        return null;
    }

    @Override
    public Integer getCurrentUserId() {
        String userId = getCookieValue("user");
        if (StringUtils.isNotBlank(userId)) {
            String u = authService.get(String.format("user:login:token:%s", userId));
            return u == null ? null : Integer.valueOf(u);
        }
        return null;
    }

}
