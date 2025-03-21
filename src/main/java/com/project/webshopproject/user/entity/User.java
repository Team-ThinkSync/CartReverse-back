package com.project.webshopproject.user.entity;

import com.project.webshopproject.common.TimeStamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", nullable = false, length = 10)
    private String username;

    @Column(name = "nickname", nullable = false, unique = true, length = 10)
    private String nickname;

    @Column(name = "email", nullable = false, unique = true, length = 30)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "address", nullable = false, length = 50)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserLoginType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private Long socialId;

    public User(String username, String nickname, String email, String password,
            String phoneNumber, String address, Grade grade, UserLoginType type, UserStatus status) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.grade = grade;
        this.type = type;
        this.status = status;
    }

    public User(String username, String nickname, String email, String password,
            String phoneNumber, String address, Grade grade, UserLoginType type, UserStatus status, Long socialId) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.grade = grade;
        this.type = type;
        this.status = status;
        this.socialId = socialId;
    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.socialId = kakaoId;
        return this;
    }

    public void changePassword(String newPassword) {
        this.password = password;
    }

    public void deleteUser() {
        this.status = UserStatus.DELETED;
    }

    public void updateProfile(final String nickname, final String phoneNumber, String address) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public void updateUser(String username, String nickname, String phoneNumber, String address) {
        this.username = username;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

}