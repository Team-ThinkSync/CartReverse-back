package com.project.webshopproject.user.dto;

import com.project.webshopproject.user.entity.Grade;
import com.project.webshopproject.user.entity.User;

public record UserGetResponseDto(
        String username,
        String email,
        String nickname,
        String phoneNumber,
        String address,
        Grade grade
) {
    public static UserGetResponseDto fromEntity(User user) {
        return new UserGetResponseDto(
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getGrade()
        );
    }
}
