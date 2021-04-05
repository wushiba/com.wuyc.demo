package com.yfshop.shop.config;

import com.yfshop.common.config.BaseValidationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xulg
 * Created in 2021-03-24 15:46
 */
@Configuration
public class ValidationConfig extends BaseValidationConfig {
    private static final Logger logger = LoggerFactory.getLogger(ValidationConfig.class);

    public ValidationConfig() {
        logger.info("**************配置类ValidationConfig被实例化*******************************");
    }

}
