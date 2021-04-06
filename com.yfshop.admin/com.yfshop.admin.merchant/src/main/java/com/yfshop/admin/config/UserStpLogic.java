package com.yfshop.admin.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.StpLogic;
import com.yfshop.common.util.BeanUtil;

public class UserStpLogic extends StpLogic {
    private SaTokenConfig saTokenConfig;

    public UserStpLogic() {
        super("login");
        saTokenConfig = BeanUtil.convert(super.getConfig(), SaTokenConfig.class);
        saTokenConfig.setTokenName("user");
    }

    @Override
    public SaTokenConfig getConfig() {

        return saTokenConfig;
    }
}
