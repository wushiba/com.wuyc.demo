package com.yfshop.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.TimeUnit;

@SpringBootApplication(exclude = {ValidationAutoConfiguration.class})
@EnableAsync
public class AdminServiceApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(AdminServiceApplication.class, args);
        String property = applicationContext.getEnvironment().getProperty("spring.profiles.active");
        System.out.println("==============启动了=====com.yfshop.admin==================环境==" + property);
        System.out.println(System.getProperty("user.dir"));
    }

}


