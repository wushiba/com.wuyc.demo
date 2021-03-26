package com.yfshop.common.accesslimit;

import cn.hutool.core.io.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Xulg
 * Created in 2020-12-05 20:09
 */
public class RedisIpVisitLimitCheckStrategy2 implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(RedisIpVisitLimitCheckStrategy2.class);

    /**
     * 限流脚本
     */
    private DefaultRedisScript<Object> ipLimitScript;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * IP是否超过单位时间的访问次数限制
     *
     * @param visitorIp       the visitor IP
     * @param durationSeconds 有效时间(秒)
     * @param limitTimes      指定时间内，API最多的请求次数
     * @return true if access legal
     */
    public boolean isIpAccessLegal(String visitorIp, int durationSeconds, int limitTimes) {
        List<String> keys = new ArrayList<>(Collections.singletonList(visitorIp));
        Object result = redisTemplate.execute(ipLimitScript, keys, String.valueOf(limitTimes), String.valueOf(durationSeconds));
        boolean isLegal = result != null && (Long) result > 0;
        logger.info(visitorIp + "------->>>" + isLegal);
        return isLegal;
    }

    @Override
    public void afterPropertiesSet() {
        // lua限流脚本
        ClassPathResource resource = new ClassPathResource("lua/IpAccessLimit.lua");
        try {
            String ipLimitScriptStr = IoUtil.read(resource.getInputStream(), "UTF-8");
            ipLimitScript = new DefaultRedisScript<>(ipLimitScriptStr, Object.class);
        } catch (IOException e) {
            throw new Error("local IpAccessLimit.lua error", e);
        }
    }

}
