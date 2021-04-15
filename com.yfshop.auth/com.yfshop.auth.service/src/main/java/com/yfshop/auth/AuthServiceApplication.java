package com.yfshop.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.Set;

@SpringBootApplication(exclude = {ValidationAutoConfiguration.class})
public class AuthServiceApplication {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(AuthServiceApplication.class, args);
        String property = applicationContext.getEnvironment().getProperty("spring.profiles.active");
        System.out.println("==============启动了=====com.yfshop.auth==================环境==" + property);
    }

    @Bean
    public ApplicationRunner applicationRunner(RedisConnectionFactory redisConnectionFactory) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) {
                try (RedisConnection connection = redisConnectionFactory.getConnection()) {
                    logger.info("==================" + connection.ping());
                    Set<byte[]> keys = connection.keys("*".getBytes());
                    if (keys != null) {
                        for (byte[] bytes : keys) {
                            logger.info(new String(bytes));
                        }
                    }
                }
            }
        };
    }
}


