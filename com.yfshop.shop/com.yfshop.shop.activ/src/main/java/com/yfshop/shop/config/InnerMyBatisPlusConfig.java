package com.yfshop.shop.config;

import com.yfshop.code.config.MyBatisPlusConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Xulg
 * Created in 2021-03-24 15:45
 */
@Configuration
@Import(MyBatisPlusConfig.class)
public class InnerMyBatisPlusConfig {
}
