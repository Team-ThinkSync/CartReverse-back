package com.project.webshopproject.auth;

import com.project.webshopproject.email.EmailService;
import com.project.webshopproject.email.RedisService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private EmailService emailService;
    private RedisService redisService;

    // 이메일로 인증번호 전송
    @PostMapping("/send-verification-code")
    public String sendVerificationCode(@RequestParam String email) throws MessagingException {
        emailService.sendVerificationCode(email);  // 인증번호 생성 및 이메일 전송
        return "인증번호를 이메일로 전송했습니다.";
    }

    // 인증번호 검증
    @PostMapping("/check-verification-code")
    public String verifyCode(@RequestParam String email, @RequestParam String inputCode) {
        String storedCode = redisService.getVerificationCode(email);
        if (storedCode != null && storedCode.equals(inputCode)) {
            return "인증 성공";
        } else {
            return "인증 실패";
        }
    }
}
