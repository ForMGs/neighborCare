package com.neighbor.care.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisTokenCycleSvc {

    private final StringRedisTemplate redisTemplate;

    public void save(Long userId, String refreshToken, long expirationSeconds){

        String key = "refresh:" + userId;
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(expirationSeconds));
    }

    public String findByUserId(Long userId){
        return redisTemplate.opsForValue().get("refresh:"+userId);
    }

    public void deleteByUserId(Long userId){
        redisTemplate.delete("refresh:"+userId);
    }

    public boolean matches(Long userId, String refreshToken){
        String saved = findByUserId(userId);
        return saved != null && saved.equals(refreshToken);
    }

}
