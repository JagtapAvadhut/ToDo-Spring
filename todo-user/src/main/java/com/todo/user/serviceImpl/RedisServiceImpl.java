package com.todo.user.serviceImpl;

import com.todo.user.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void saveWithTimeToDelete(String key, Object value,Long timeToDelete) {
        redisTemplate.opsForValue().set(key, value,timeToDelete, TimeUnit.SECONDS);
    }
    @Override
    public Object find(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }


}

