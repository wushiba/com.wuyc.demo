package com.yfshop.admin.config;

import com.github.benmanes.caffeine.cache.Caffeine;
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

    private static final long AVAILABLE_TIME = 10;

//    @Bean
//    @Profile(value = {"dev", "uat"})
    public CaffeineCacheManager cacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                // 最后一次写入后经过固定时间过期
                .expireAfterWrite(AVAILABLE_TIME, TimeUnit.MINUTES)
                // 缓存的最大条数
                .maximumSize(1000);
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setAllowNullValues(true);
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

}
