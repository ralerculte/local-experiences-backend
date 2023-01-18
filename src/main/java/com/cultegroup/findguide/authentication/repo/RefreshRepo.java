package com.cultegroup.findguide.authentication.repo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshRepo {
    private final RedisTemplate<String, String> template;

    public RefreshRepo(@Qualifier(value = "redis") RedisTemplate<String, String> template) {
        this.template = template;
    }

    public void set(String email, String token) {
        template.opsForValue().set(email, token);
    }

    public String get(String email) {
        return template.opsForValue().get(email);
    }
}
