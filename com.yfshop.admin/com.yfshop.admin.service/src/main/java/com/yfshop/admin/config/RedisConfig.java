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

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter, MessageListenerAdapter listenerFinishAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //订阅多个频道
        container.addMessageListener(listenerAdapter, new PatternTopic("actCodeTask"));
        container.addMessageListener(listenerFinishAdapter, new PatternTopic("actCodeTaskFinish"));
        //序列化对象（特别注意：发布的时候需要设置序列化；订阅方也需要设置序列化）
        Jackson2JsonRedisSerializer seria = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        seria.setObjectMapper(objectMapper);
        container.setTopicSerializer(seria);
        return container;
    }

    //表示监听一个频道
    @Bean
    MessageListenerAdapter listenerAdapter(ActCodeConsume receiver) {
        return new MessageListenerAdapter(receiver, "getMessage");
    }

    //表示监听一个频道
    @Bean
    MessageListenerAdapter listenerFinishAdapter(ActCodeConsume receiver) {
        return new MessageListenerAdapter(receiver, "finish");
    }

}
