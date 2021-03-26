package com.yfshop.admin.config;

import com.yfshop.code.config.MyBatisPlusConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Xulg
 * Created in 2021-03-24 15:45
 */
@Configuration
@Import(MyBatisPlusConfig.class)
public class InnerMyBatisPlusConfig {
    private static final Logger logger = LoggerFactory.getLogger(InnerMyBatisPlusConfig.class);

    public InnerMyBatisPlusConfig() {
        logger.info("**************配置类InnerMyBatisPlusConfig被实例化*******************************");
    }
}
