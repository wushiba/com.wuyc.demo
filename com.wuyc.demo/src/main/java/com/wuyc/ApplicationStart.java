package com.wuyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author sp0313
 * @date 2022年11月28日 09:41:00
 */
@SpringBootApplication
public class ApplicationStart {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationStart.class, args);
        System.out.println("===================================run success");
    }

}
