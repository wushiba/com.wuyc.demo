package com.yfshop.auth.api.service;

import java.util.List;

public interface AuthService {

    String get(String key);

    void set(String key, String value, long timeout);

    void update(String key, String value);

    void delete(String key);

    long getTimeout(String key);

    void updateTimeout(String key, long timeout);

    Object getObject(String key);

    void setObject(String key, Object object, long timeout);

    void updateObject(String key, Object object);

    void deleteObject(String key);

    long getObjectTimeout(String key);

    void updateObjectTimeout(String key, long timeout);

    List<String> searchData(String prefix, String keyword, int start, int size);
}
