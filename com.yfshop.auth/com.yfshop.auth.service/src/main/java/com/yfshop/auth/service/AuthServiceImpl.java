package com.yfshop.auth.service;

import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import com.yfshop.auth.api.service.AuthService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    @Autowired
    public SaTokenDaoRedisJackson saTokenDaoRedisJackson;


    @Override
    public String get(String key) {
        logger.debug("get key-->{}",key);
        return saTokenDaoRedisJackson.get(key);
    }

    @Override
    public void set(String key, String value, long timeout) {
        logger.debug("set key-->{},value-->{},value-->{}",key,value,timeout);
        saTokenDaoRedisJackson.set(key, value, timeout);

    }

    @Override
    public void update(String key, String value) {
        logger.debug("update key-->{},value-->{},value-->{}",key,value);
        saTokenDaoRedisJackson.update(key, value);
    }

    @Override
    public void delete(String key) {
        logger.debug("delete key-->{}",key);
        saTokenDaoRedisJackson.delete(key);
    }

    @Override
    public long getTimeout(String key) {
        logger.debug("getTimeout key-->{}",key);
        return saTokenDaoRedisJackson.getTimeout(key);
    }

    @Override
    public void updateTimeout(String key, long timeout) {
        logger.debug("updateTimeout key-->{},timeout-->{}",key,timeout);
        saTokenDaoRedisJackson.updateTimeout(key, timeout);
    }

    @Override
    public Object getObject(String key) {
        logger.debug("getObject key-->{}",key);
        return saTokenDaoRedisJackson.getObject(key);
    }

    @Override
    public void setObject(String key, Object object, long timeout) {
        logger.debug("setObject key-->{}",key);
        saTokenDaoRedisJackson.setObject(key, object, timeout);
    }

    @Override
    public void updateObject(String key, Object object) {
        logger.debug("updateObject key-->{}",key);
        saTokenDaoRedisJackson.updateObject(key,object);
    }

    @Override
    public void deleteObject(String key) {
        logger.debug("deleteObject key-->{}",key);
        this.saTokenDaoRedisJackson.deleteObject(key);
    }

    @Override
    public long getObjectTimeout(String key) {
        logger.debug("getObjectTimeout key-->{}",key);
        return this.saTokenDaoRedisJackson.getObjectTimeout(key);
    }

    @Override
    public void updateObjectTimeout(String key, long timeout) {
        logger.debug("updateObjectTimeout key-->{}",key);
        saTokenDaoRedisJackson.updateObjectTimeout(key,timeout);
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size) {
        logger.debug("searchData prefix-->{}",prefix);
        return saTokenDaoRedisJackson.searchData(prefix,keyword,start,size);
    }
}
