package com.yfshop.shop.controller;

import com.yfshop.auth.api.service.AuthService;
import com.yfshop.common.base.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;

public abstract class AbstractBaseController implements BaseController {

    @DubboReference
    AuthService authService;

    @Override
    public String getCurrentOpenId() {
        String openId = getCookieValue("yfopen");
        if (StringUtils.isNotBlank(openId)) {
            String id =authService.get(String.format("yfopen:login:token:%s", openId));
            return id;
        }
        return null;
    }

}
