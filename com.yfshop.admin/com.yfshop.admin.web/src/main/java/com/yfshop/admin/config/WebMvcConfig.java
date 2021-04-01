package com.yfshop.admin.config;

import com.yfshop.common.accesslimit.IpVisitLimitInterceptor;
import com.yfshop.common.accesslimit.RedisIpVisitLimitCheckStrategy;
import com.yfshop.common.config.BaseWebMvcConfig;
import com.yfshop.common.log.WebLogAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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

    @Profile("dev")
    @Bean
    public WebLogAspect webLogAspect() {
        return new WebLogAspect();
    }

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
