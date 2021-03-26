package com.yfshop.shop.config;

import com.yfshop.common.accesslimit.IpVisitLimitInterceptor;
import com.yfshop.common.accesslimit.RedisIpVisitLimitCheckStrategy;
import com.yfshop.common.config.BaseWebMvcConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import javax.annotation.Nonnull;

/**
 * WebMVC的配置
 *
 * @author Xulg
 * Created in 2021-03-22 9:44
 */
@Configuration
public class WebMvcConfig extends BaseWebMvcConfig {

    @Bean
    public RedisIpVisitLimitCheckStrategy redisIpVisitLimitCheckStrategy() {
        return new RedisIpVisitLimitCheckStrategy();
    }

    @Bean
    public IpVisitLimitInterceptor ipVisitLimitInterceptor() {
        return new IpVisitLimitInterceptor();
    }

    @Override
    public void addInterceptors(@Nonnull InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(ipVisitLimitInterceptor());
    }

}
