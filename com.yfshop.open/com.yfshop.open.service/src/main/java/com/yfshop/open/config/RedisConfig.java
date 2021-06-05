package com.yfshop.open.config;

import com.yfshop.common.config.BaseRedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Redis相关配置
 */
@Configuration
public class RedisConfig extends BaseRedisConfig {
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    public RedisConfig() {
        logger.info("**************配置类RedisConfig被实例化*******************************");
    }

}
