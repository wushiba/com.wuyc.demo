package com.yfshop.auth.service;

import cn.dev33.satoken.dao.SaTokenDao;
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
    public ObjectMapper objectMapper;
    @Autowired
    public StringRedisTemplate stringRedisTemplate;
    public RedisTemplate<String, Object> objectRedisTemplate;


    @Autowired
    public void setObjectRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
        try {
            Field field = GenericJackson2JsonRedisSerializer.class.getDeclaredField("mapper");
            field.setAccessible(true);
            ObjectMapper objectMapper = (ObjectMapper)field.get(valueSerializer);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            this.objectMapper = objectMapper;
        } catch (Exception var6) {
            System.err.println(var6.getMessage());
        }

        RedisTemplate<String, Object> template = new RedisTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();
        if (this.objectRedisTemplate == null) {
            this.objectRedisTemplate = template;
        }

    }

    @Override
    public String get(String key) {
        return (String)this.stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void set(String key, String value, long timeout) {
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            this.stringRedisTemplate.opsForValue().set(key, value);
        } else {
            this.stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        }

    }

    @Override
    public void update(String key, String value) {
        long expire = this.getTimeout(key);
        if (expire != SaTokenDao.NOT_VALUE_EXPIRE) {
            this.set(key, value, expire);
        }
    }

    @Override
    public void delete(String key) {
        this.stringRedisTemplate.delete(key);
    }

    @Override
    public long getTimeout(String key) {
        return this.stringRedisTemplate.getExpire(key);
    }

    @Override
    public void updateTimeout(String key, long timeout) {
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = this.getTimeout(key);
            if (expire != SaTokenDao.NEVER_EXPIRE) {
                this.set(key, this.get(key), timeout);
            }

        } else {
            this.stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        }
    }

    @Override
    public Object getObject(String key) {
        return this.objectRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            this.objectRedisTemplate.opsForValue().set(key, object);
        } else {
            this.objectRedisTemplate.opsForValue().set(key, object, timeout, TimeUnit.SECONDS);
        }

    }

    @Override
    public void updateObject(String key, Object object) {
        long expire = this.getObjectTimeout(key);
        if (expire != SaTokenDao.NOT_VALUE_EXPIRE) {
            this.setObject(key, object, expire);
        }
    }

    @Override
    public void deleteObject(String key) {
        this.objectRedisTemplate.delete(key);
    }

    @Override
    public long getObjectTimeout(String key) {
        return this.objectRedisTemplate.getExpire(key);
    }

    @Override
    public void updateObjectTimeout(String key, long timeout) {
        if (timeout == SaTokenDao.NEVER_EXPIRE) {
            long expire = this.getObjectTimeout(key);
            if (expire != SaTokenDao.NEVER_EXPIRE) {
                this.setObject(key, this.getObject(key), timeout);
            }

        } else {
            this.objectRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        }
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size) {
        Set<String> keys = this.stringRedisTemplate.keys(prefix + "*" + keyword + "*");
        List<String> list = new ArrayList(keys);
        return SaTokenInsideUtil.searchList(list, start, size);
    }
}
