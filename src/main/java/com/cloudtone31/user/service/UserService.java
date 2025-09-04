package com.cloudtone31.user.service;

import com.cloudtone31.user.domain.User;

import com.cloudtone31.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User updateNickname(long kakaoId, String nickname) {
        User u = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        u.updateNickname(nickname);  // 엔티티 메서드 호출
        return u; // JPA 변경 감지로 자동 UPDATE 실행됨
    }
}