package com.yfshop.admin.config;

import cn.dev33.satoken.dao.SaTokenDao;
import com.yfshop.auth.api.service.AuthService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SaTokenDaoConfig implements SaTokenDao {

    @DubboReference
    public AuthService authService;

    public SaTokenDaoConfig() {
    }

    @Override
    public String get(String key) {
        return authService.get(key);
    }

    @Override
    public void set(String key, String value, long timeout) {
        authService.set(key, value, timeout);
    }

    @Override
    public void update(String key, String value) {
        authService.update(key, value);
    }

    @Override
    public void delete(String key) {
        this.authService.delete(key);
    }

    @Override
    public long getTimeout(String key) {
        return this.authService.getTimeout(key);
    }

    @Override
    public void updateTimeout(String key, long timeout) {
        authService.updateTimeout(key, timeout);
    }

    @Override
    public Object getObject(String key) {
        return authService.getObject(key);
    }

    @Override
    public void setObject(String key, Object object, long timeout) {
        authService.setObject(key, object, timeout);
    }

    @Override
    public void updateObject(String key, Object object) {
        authService.updateObject(key, object);
    }

    @Override
    public void deleteObject(String key) {
        authService.deleteObject(key);
    }

    @Override
    public long getObjectTimeout(String key) {
        return authService.getObjectTimeout(key);
    }

    @Override
    public void updateObjectTimeout(String key, long timeout) {
        authService.updateObjectTimeout(key, timeout);
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size) {
        return authService.searchData(prefix, keyword, start, size);
    }
}
