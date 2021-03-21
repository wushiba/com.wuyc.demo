package com.yfshop.shop.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@MapperScan("com.yfshop.shop")
@SpringBootApplication
public class ShopServiceApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(ShopServiceApplication.class, args);
		String property = applicationContext.getEnvironment().getProperty("spring.profiles.active");
		System.out.println("==============启动了=====com.jf.front.service==================环境===" + property);
	}
}


