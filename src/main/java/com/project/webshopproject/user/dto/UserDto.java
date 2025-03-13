package com.project.webshopproject.user.dto;

import com.project.webshopproject.user.entity.Grade;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    private String userName;
    private String email;
    private String nickName;
    private String phoneNumber;
    private String address;
    private Grade grade;
}
