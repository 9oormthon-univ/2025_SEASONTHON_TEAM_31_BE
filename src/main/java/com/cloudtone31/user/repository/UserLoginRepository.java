package com.cloudtone31.user.repository;


import com.cloudtone31.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLoginRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(Long kakaoId);
}