package com.yfshop.admin.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yfshop.admin.task.ActCodeConsume;
import com.yfshop.admin.task.ActCodeTask;
import com.yfshop.common.config.BaseRedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

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
