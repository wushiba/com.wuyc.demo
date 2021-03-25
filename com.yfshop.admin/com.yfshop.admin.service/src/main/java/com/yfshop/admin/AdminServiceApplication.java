package com.yfshop.admin;

import com.alibaba.fastjson.JSON;
import com.yfshop.code.mapper.MerchantMapper;
import com.yfshop.code.mapper.custom.CustomMerchantMapper;
import com.yfshop.code.model.Merchant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication(exclude = {ValidationAutoConfiguration.class})
public class AdminServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(AdminServiceApplication.class, args);
        String property = applicationContext.getEnvironment().getProperty("spring.profiles.active");
        System.out.println("==============启动了=====com.yfshop.admin==================环境==" + property);
        try {
            CustomMerchantMapper customMerchantMapper = applicationContext.getBean(CustomMerchantMapper.class);
            List<Merchant> merchants = customMerchantMapper.queryAll();
            System.out.println(JSON.toJSONString(merchants, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            MerchantMapper merchantMapper = applicationContext.getBean(MerchantMapper.class);
            List<Merchant> merchants = merchantMapper.selectList(null);
            System.out.println(JSON.toJSONString(merchants, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


