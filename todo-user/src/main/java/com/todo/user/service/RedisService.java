package com.todo.user.service;

public interface RedisService {
    void save(String key, Object value);

    void saveWithTimeToDelete(String key, Object value, Long timeToDelete);

    Object find(String key);

    void delete(String key);
}
