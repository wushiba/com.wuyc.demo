package com.yfshop.common.accesslimit;

import cn.hutool.core.io.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;

/**
 * @author Xulg
 * Created in 2020-12-05 20:09
 */
public class RedisIpVisitLimitCheckStrategy implements InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(RedisIpVisitLimitCheckStrategy2.class);

    private JedisPool jedisPool;
    private String ipLimitScript;

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private int redisPort;
    /**
     * IP是否超过单位时间的访问次数限制
     *
     * @param visitorIp       the visitor IP
     * @param durationSeconds 有效时间(秒)
     * @param limitTimes      指定时间内，API最多的请求次数
     * @return true if access legal
     */
    public boolean isIpAccessLegal(String visitorIp, int durationSeconds, int limitTimes) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.eval(ipLimitScript, 1, visitorIp,
                    String.valueOf(limitTimes), String.valueOf(durationSeconds));
            return result != null && (Long) result > 0;
        }
    }

    @Override
    public void afterPropertiesSet() {
        Assert.hasText(redisHost, "redis host must not be null");
        Assert.isTrue(redisPort > 0, "redis port must available");
        // lua限流脚本
        ClassPathResource resource = new ClassPathResource("lua/IpAccessLimit.lua");
        try {
            ipLimitScript = IoUtil.read(resource.getInputStream(), "UTF-8");
        } catch (IOException e) {
            throw new Error("local IpAccessLimit.lua error", e);
        }
        // init jedis pool
        jedisPool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort);
    }

    @Override
    public void destroy() {
        if (jedisPool != null) {
            jedisPool.destroy();
        }
        logger.info(this.getClass().getName() + "#destroy() jedis pool destroyed");
    }
}
