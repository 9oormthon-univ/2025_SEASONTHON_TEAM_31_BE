package com.cloudtone31.user.service;

import com.cloudtone31.user.domain.User;

import com.cloudtone31.user.repository.UserLoginRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserLoginRepository userLoginRepository;

    @Transactional
    public User updateNicknameByUserId(Long userId, String nickname) {
        User u = userLoginRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        u.updateNickname(nickname);
        return userLoginRepository.save(u);
    }
}