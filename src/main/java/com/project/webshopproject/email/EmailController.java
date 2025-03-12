package com.project.webshopproject.email;

import com.project.webshopproject.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {
    private RedisService redisService;
    private UserService userService;

    @PostMapping("/auth/forget-email")
    public String findEmail(@RequestParam String userName, @RequestParam String phoneNumber,
            @RequestParam String code) {
        // 인증번호 검증
        String storedCode = redisService.getVerificationCode(phoneNumber);
        if (storedCode != null && storedCode.equals(code)) {
            // 이메일 찾기 로직 (사용자 이름과 전화번호로 조회)
            String email = userService.findEmailByUserNameAndPhoneNumber(userName, phoneNumber);
            if (email != null) {
                return "이메일은 " + email + " 입니다.";
            } else {
                return "해당 사용자 이름과 전화번호에 대한 이메일을 찾을 수 없습니다.";
            }
        } else {
            return "인증 실패";
        }
    }
}
