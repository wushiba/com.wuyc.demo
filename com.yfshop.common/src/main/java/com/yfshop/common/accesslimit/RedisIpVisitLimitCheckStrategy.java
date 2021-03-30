package com.yfshop.common.accesslimit;

import cn.hutool.core.io.IoUtil;
import com.yfshop.common.exception.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.ReturnType;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author Xulg
 * Created in 2020-12-05 20:09
 */
public class RedisIpVisitLimitCheckStrategy implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(RedisIpVisitLimitCheckStrategy.class);

    private String ipLimitScript;

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * IP是否超过单位时间的访问次数限制
     *
     * @param visitorIp       the visitor IP
     * @param durationSeconds 有效时间(秒)
     * @param limitTimes      指定时间内，API最多的请求次数
     * @return true if access legal
     */
    public boolean isIpAccessLegal(String visitorIp, int durationSeconds, int limitTimes) {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            Long result = connection.eval(ipLimitScript.getBytes(), ReturnType.INTEGER, 1, visitorIp.getBytes(),
                    String.valueOf(limitTimes).getBytes(), String.valueOf(durationSeconds).getBytes());
            return result != null && result > 0;
        }
    }

    @Override
    public void afterPropertiesSet() {
        // lua限流脚本
        ClassPathResource resource = new ClassPathResource("lua/IpAccessLimit.lua");
        try {
            ipLimitScript = IoUtil.read(resource.getInputStream(), "UTF-8");
        } catch (IOException e) {
            throw new Error("local IpAccessLimit.lua error", e);
        }
        Asserts.assertNonNull(redisConnectionFactory, 500, "redisConnectionFactory must not be null");
    }

}
