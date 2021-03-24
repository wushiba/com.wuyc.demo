package com.yfshop.shop;

import com.yfshop.shop.service.address.UserAddressService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = {ValidationAutoConfiguration.class})
public class ShopServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ShopServiceApplication.class, args);
        String property = applicationContext.getEnvironment().getProperty("spring.profiles.active");
        System.out.println("==============启动了=====com.yfshop.shop.service==================环境===" + property);
        try {
            UserAddressService addressService = applicationContext.getBean(UserAddressService.class);
            System.out.println(addressService.queryUserAddresses(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


