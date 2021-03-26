package com.yfshop.admin.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.session.SaSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * sa-token持久层的实现类 , 基于redis
 * @author kong
 *
 */
@Component
public class SaTokenDaoRedis implements SaTokenDao {

	/**
	 * string专用
	 */
	@Autowired
	StringRedisTemplate stringRedisTemplate;

	/**
	 * SaSession专用
	 */
	RedisTemplate<String, SaSession> redisTemplate;
	@Autowired
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setRedisTemplate(RedisTemplate redisTemplate) {
		RedisSerializer stringSerializer = new StringRedisSerializer();
	    redisTemplate.setKeySerializer(stringSerializer);
	    JdkSerializationRedisSerializer jrSerializer = new JdkSerializationRedisSerializer();
	    redisTemplate.setValueSerializer(jrSerializer);
		this.redisTemplate = redisTemplate;
	}


	/**
	 * 根据key获取value ，如果没有，则返回空
	 */
	@Override
	public String get(String key) {
		// System.out.println(key + "====" + stringRedisTemplate.opsForValue().get(key));
		return stringRedisTemplate.opsForValue().get(key);
	}

	/**
	 * 写入指定key-value键值对，并设定过期时间(单位：秒)
	 */
	@Override
	public void set(String key, String value, long timeout) {
		stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
	}

    @Override
    public void update(String s, String s1) {

    }

    /**
	 * 删除一个指定的key
	 */
	@Override
	public void delete(String key) {
		stringRedisTemplate.delete(key);
	}

    @Override
    public long getTimeout(String s) {
        return 0;
    }

    @Override
    public void updateTimeout(String s, long l) {

    }

    @Override
    public Object getObject(String s) {
        return null;
    }

    @Override
    public void setObject(String s, Object o, long l) {

    }

    @Override
    public void updateObject(String s, Object o) {

    }

    @Override
    public void deleteObject(String s) {

    }

    @Override
    public long getObjectTimeout(String s) {
        return 0;
    }

    @Override
    public void updateObjectTimeout(String s, long l) {

    }

    /**
	 * 根据指定key的session，如果没有，则返回空
	 */
	@Override
	public SaSession getSession(String sessionId) {
		return redisTemplate.opsForValue().get(sessionId);
	}

	/**
	 * 将指定session持久化
	 */
	@Override
	public void setSession(SaSession session, long timeout) {
		redisTemplate.opsForValue().set(session.getId(), session, timeout, TimeUnit.SECONDS);
	}

	/**
	 * 更新指定session
	 */
	@Override
	public void updateSession(SaSession session) {
		long expire = redisTemplate.getExpire(session.getId());
		// -2 = 无此键
		if(expire == -2) {
			return;
		}
		redisTemplate.opsForValue().set(session.getId(), session, expire, TimeUnit.SECONDS);
	}

	/**
	 * 删除一个指定的session
	 */
	@Override
	public void deleteSession(String sessionId) {
		redisTemplate.delete(sessionId);
	}

    @Override
    public List<String> searchData(String s, String s1, int i, int i1) {
        return null;
    }
}