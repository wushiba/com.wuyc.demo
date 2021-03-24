package com.yfshop.admin;

import com.yfshop.admin.api.mall.AdminMallManageService;
import com.yfshop.admin.api.mall.request.CreateBannerReq;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = {ValidationAutoConfiguration.class})
public class AdminServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(AdminServiceApplication.class, args);
        String property = applicationContext.getEnvironment().getProperty("spring.profiles.active");
        try {
            Object caffeineCacheManager = applicationContext.getBean("caffeineCacheManager");
        } catch (Exception e) {
            e.printStackTrace();
        }
        AdminMallManageService adminMallManageService = applicationContext.getBean(AdminMallManageService.class);
        try {
            adminMallManageService.createBanner(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            CreateBannerReq req = new CreateBannerReq();
            adminMallManageService.createBanner(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            adminMallManageService.deleteItem(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            adminMallManageService.deleteItem(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("==============启动了=====com.yfshop.admin==================环境==" + property);
    }

}


