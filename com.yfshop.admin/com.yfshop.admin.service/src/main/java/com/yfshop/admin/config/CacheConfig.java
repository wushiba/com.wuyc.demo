package com.yfshop.admin.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.TimeUnit;

/**
 * @author Xulg
 * Created in 2021-03-23 16:20
 */
@Configuration
@EnableCaching
public class CacheConfig {
    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    private static final long AVAILABLE_TIME = 10;
    private static final long MAXIMUM_SIZE = 1000;

    public CacheConfig() {
        logger.info("**************配置类CacheConfig被实例化*******************************");
    }

    @Bean
    @Profile("dev")
    public CaffeineCacheManager cacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                // 最后一次写入后经过固定时间过期
                .expireAfterWrite(AVAILABLE_TIME, TimeUnit.MINUTES)
                // 缓存的最大条数
                .maximumSize(MAXIMUM_SIZE);
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setAllowNullValues(true);
        caffeineCacheManager.setCaffeine(caffeine);
        logger.info("*************************创建CaffeineCacheManager************************");
        return caffeineCacheManager;
    }

}
