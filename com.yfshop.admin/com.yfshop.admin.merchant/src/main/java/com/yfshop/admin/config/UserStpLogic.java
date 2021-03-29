package com.yfshop.admin.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.StpLogic;
import com.yfshop.common.util.BeanUtil;

public class UserStpLogic extends StpLogic {
    private SaTokenConfig saTokenConfig;

    public UserStpLogic(String loginKey) {
        super(loginKey);
        saTokenConfig = BeanUtil.convert(super.getConfig(), SaTokenConfig.class);
        saTokenConfig.setCookieDomain("shop");
    }

    @Override
    public SaTokenConfig getConfig() {

        return saTokenConfig;
    }
}
