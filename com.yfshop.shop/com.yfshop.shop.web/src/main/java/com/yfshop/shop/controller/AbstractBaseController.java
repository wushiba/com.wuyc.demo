package com.yfshop.shop.controller;

import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import com.yfshop.common.base.BaseController;
import com.yfshop.shop.config.WxStpLogic;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractBaseController implements BaseController {

    @Autowired
    SaTokenDaoRedisJackson saTokenDaoRedis;

    @Override
    public String getCurrentOpenId() {
        String openId = getCookieValue("yfopen");
        if (StringUtils.isNotBlank(openId)) {
            String id =saTokenDaoRedis.get(String.format("yfopen:login:token:%s", openId));
            return id;
        }
        return null;
    }

}
