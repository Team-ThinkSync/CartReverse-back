package com.project.webshopproject.auth;

import com.project.webshopproject.email.EmailService;
import com.project.webshopproject.email.RedisService;
import com.project.webshopproject.user.dto.UserLoginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @PostMapping("/login")
    public void login(
            @RequestBody UserLoginRequestDto loginRequest
    ) {
        // 실제로는 Security Filter에서 처리됨
        throw new IllegalStateException("Swagger 문서용 설명용 API입니다. 사용하지 마세요.");
    }

}
