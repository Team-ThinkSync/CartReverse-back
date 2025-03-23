package com.project.webshopproject.user.dto;

public record UserUpdateRequestDto(
        String username,
        String nickname,
        String phoneNumber,
        String address
) {}
