package com.yfshop.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class ShopWebApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(ShopWebApplication.class, args);
		String property = applicationContext.getEnvironment().getProperty("spring.profiles.active");
		System.out.println("==============启动了=====com.yfshop.shop.web==================环境===" + property);
	}
}


