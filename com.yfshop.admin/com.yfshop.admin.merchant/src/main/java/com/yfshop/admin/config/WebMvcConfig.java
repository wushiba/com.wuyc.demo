package com.yfshop.admin.config;

import com.yfshop.common.config.BaseWebMvcConfig;
import com.yfshop.common.log.WebSystemOperateLogAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WebMVC的配置
 *
 * @author Xulg
 * Created in 2021-03-22 9:44
 */
@Configuration
public class WebMvcConfig extends BaseWebMvcConfig {

    @Bean
    public WebSystemOperateLogAspect webSystemOperateLogAspect() {
        return new WebSystemOperateLogAspect();
    }

}
