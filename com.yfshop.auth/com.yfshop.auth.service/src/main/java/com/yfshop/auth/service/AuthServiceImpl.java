package com.yfshop.auth.service;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import cn.dev33.satoken.util.SaTokenInsideUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yfshop.auth.api.service.AuthService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@DubboService
public class AuthServiceImpl implements AuthService {
    @Autowired
    public SaTokenDaoRedisJackson saTokenDaoRedisJackson;


    @Override
    public String get(String key) {
        return saTokenDaoRedisJackson.get(key);
    }

    @Override
    public void set(String key, String value, long timeout) {
        saTokenDaoRedisJackson.set(key, value, timeout);

    }

    @Override
    public void update(String key, String value) {
        saTokenDaoRedisJackson.update(key, value);
    }

    @Override
    public void delete(String key) {
        saTokenDaoRedisJackson.delete(key);
    }

    @Override
    public long getTimeout(String key) {
        return saTokenDaoRedisJackson.getTimeout(key);
    }

    @Override
    public void updateTimeout(String key, long timeout) {
        saTokenDaoRedisJackson.updateTimeout(key, timeout);
    }

    @Override
    public Object getObject(String key) {
        return saTokenDaoRedisJackson.getObject(key);
    }

    @Override
    public void setObject(String key, Object object, long timeout) {
        saTokenDaoRedisJackson.setObject(key, object, timeout);
    }

    @Override
    public void updateObject(String key, Object object) {
        saTokenDaoRedisJackson.updateObject(key,object);
    }

    @Override
    public void deleteObject(String key) {
        this.saTokenDaoRedisJackson.deleteObject(key);
    }

    @Override
    public long getObjectTimeout(String key) {
        return this.saTokenDaoRedisJackson.getObjectTimeout(key);
    }

    @Override
    public void updateObjectTimeout(String key, long timeout) {
        saTokenDaoRedisJackson.updateObjectTimeout(key,timeout);
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size) {

        return saTokenDaoRedisJackson.searchData(prefix,keyword,start,size);
    }
}
