package com.project.webshopproject.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private JavaMailSender mailSender;
    private RedisService redisService;  // Redis 서비스

    // 인증번호를 생성하고 이메일을 전송하는 메소드
    public void sendVerificationCode(String email) throws MessagingException {
        String verificationCode = generateVerificationCode();  // 인증번호 생성
        saveVerificationCodeInRedis(email, verificationCode);  // Redis에 저장
        sendEmail(email, verificationCode);  // 이메일 전송
    }

    // 인증번호 생성 (6자리 랜덤 숫자)
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    // Redis에 인증번호 저장
    private void saveVerificationCodeInRedis(String email, String verificationCode) {
        redisService.saveVerificationCode(email, verificationCode);  // RedisService는 아래에서 구현
    }

    // 이메일로 인증번호 보내기
    private void sendEmail(String email, String verificationCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("이메일 인증번호");
        helper.setText("인증번호: " + verificationCode);
        mailSender.send(message);
    }
}
