package com.yfshop.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = {ValidationAutoConfiguration.class})
@EnableAsync
public class AdminReportApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(AdminReportApplication.class, args);
        String property = applicationContext.getEnvironment().getProperty("spring.profiles.active");
        System.out.println("==============启动了=====com.yfshop.admin.report==================环境==" + property);
    }
}


