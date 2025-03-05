package com.project.webshopproject.user;

import com.project.webshopproject.user.dto.MyPageResponseDto;
import com.project.webshopproject.user.dto.UserChangePasswordRequestDto;
import com.project.webshopproject.user.dto.UserResignRequestDto;
import com.project.webshopproject.user.dto.UserSignupRequestDto;
import com.project.webshopproject.user.entity.Grade;
import com.project.webshopproject.user.entity.User;
import com.project.webshopproject.user.entity.UserLoginType;
import com.project.webshopproject.user.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j(topic = "User Service")
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(final UserSignupRequestDto requestDto) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
        // 닉네임 중복 체크
        if (userRepository.existsByNickname(requestDto.nickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User user = new User(
                requestDto.username(),
                requestDto.nickname(),
                requestDto.email(),
                passwordEncoder.encode(requestDto.password()),
                requestDto.phoneNumber(),
                requestDto.address(),
                Grade.BASIC,
                UserLoginType.COMMON,
                UserStatus.ACTIVE
        );
        userRepository.save(user);
    }

    public void changePassword(UserChangePasswordRequestDto requestDto, String email) {
        User user = findByEmail(email);
        // 현재 비밀번호가 올바른지 확인
        if (!passwordEncoder.matches(requestDto.password(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }
        user.changePassword(passwordEncoder.encode(requestDto.newPassword()));
    }

    public void resign(UserResignRequestDto requestDto, String email) {
        User user = findByEmail(email);
        // 현재 비밀번호가 올바른지 확인
        if (!passwordEncoder.matches(requestDto.password(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }
        user.deleteUser();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("사용자를 찾지 못함 | request : {}", email);
            return new UsernameNotFoundException("사용자를 찾지 못했습니다.");
        });
    }

    public MyPageResponseDto myPageResponseDto(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("사용자가 존재하지않습니다."));
        return new MyPageResponseDto(user);
    }
}
