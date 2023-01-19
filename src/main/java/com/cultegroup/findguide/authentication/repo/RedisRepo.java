package com.cultegroup.findguide.authentication.repo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class RedisRepo {
    private final RedisTemplate<String, String> template;

    public RedisRepo(@Qualifier(value = "redis") RedisTemplate<String, String> template) {
        this.template = template;
    }

    public void set(String key, String token, Duration timeout) {
        template.opsForValue().set(key, token, timeout);
    }

    public void delete(String key) {
        template.delete(key);
    }

    public String get(String key) {
        return template.opsForValue().get(key);
    }
}
