package com.project.webshopproject.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private StringRedisTemplate redisTemplate;

    // 인증번호를 Redis에 저장
    public void saveVerificationCode(String email, String verificationCode) {
        redisTemplate.opsForValue().set(email, verificationCode);
        // Redis에 인증번호를 5분 동안 유효하게 설정
        redisTemplate.expire(email, 5, java.util.concurrent.TimeUnit.MINUTES);
    }

    // Redis에서 인증번호 조회
    public String getVerificationCode(String email) {
        return redisTemplate.opsForValue().get(email);
    }
}
