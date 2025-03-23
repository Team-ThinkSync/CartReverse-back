package com.project.webshopproject.user;

import com.project.webshopproject.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByUserId(Long userId);

    Optional<User> findBySocialId(Long kakaoId);

    Optional<User> findByUserNameAndPhoneNumber(String userName, String phoneNumber);
}
