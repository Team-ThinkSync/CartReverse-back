package com.project.webshopproject.controller;

import com.project.webshopproject.service.EmailService;
import com.project.webshopproject.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisService redisService;

    // 이메일로 인증번호 전송
    @PostMapping("/auth/send-verification-code")
    public String sendVerificationCode(@RequestParam String email) throws MessagingException {
        emailService.sendVerificationCode(email);  // 인증번호 생성 및 이메일 전송
        return "인증번호를 이메일로 전송했습니다.";
    }

    // 인증번호 검증
    @PostMapping("/auth/check-verification-code")
    public String verifyCode(@RequestParam String email, @RequestParam String inputCode) {
        String storedCode = redisService.getVerificationCode(email);
        if (storedCode != null && storedCode.equals(inputCode)) {
            return "인증 성공";
        } else {
            return "인증 실패";
        }
    }
}
