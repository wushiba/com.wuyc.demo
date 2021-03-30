package com.yfshop.admin.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.StpLogic;
import com.yfshop.common.util.BeanUtil;

public class WxStpLogic extends StpLogic {
    private SaTokenConfig saTokenConfig;

    public WxStpLogic(String loginKey) {
        super(loginKey);
        saTokenConfig = BeanUtil.convert(super.getConfig(), SaTokenConfig.class);
        saTokenConfig.setCookieDomain("yfOpen");
        saTokenConfig.setTokenName("yfOpen");
    }

    @Override
    public SaTokenConfig getConfig() {

        return saTokenConfig;
    }
}
