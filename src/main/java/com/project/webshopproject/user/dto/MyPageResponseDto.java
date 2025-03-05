package com.project.webshopproject.user.dto;

import com.project.webshopproject.user.entity.Grade;
import com.project.webshopproject.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageResponseDto {
    private String userName;
    private String email;
    private String nickName;
    private String phoneNumber;
    private String address;
    private Grade grade;

    public MyPageResponseDto(User user){
        this.userName = user.getUsername();
        this.email = user.getEmail();
        this.nickName = user.getNickname();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.grade = user.getGrade();
    }
}
