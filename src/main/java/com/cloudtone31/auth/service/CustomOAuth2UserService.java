package com.cloudtone31.auth.service;

import com.cloudtone31.auth.KakaoAttributes;
import com.cloudtone31.user.domain.User;

import com.cloudtone31.user.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserLoginRepository userLoginRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attrs = oAuth2User.getAttributes();

        Long kakaoId = KakaoAttributes.getId(attrs);
        String email = KakaoAttributes.getEmail(attrs);
        String nickname = KakaoAttributes.getNickname(attrs);
        String profile = KakaoAttributes.getProfileImage(attrs);

        // Upsert
        User user = userLoginRepository.findByKakaoId(kakaoId)
                .map(u -> {
                    // 필요한 필드만 업데이트
                    u = User.builder()
                            .id(u.getId())
                            .kakaoId(u.getKakaoId())
                            .email(email != null ? email : u.getEmail())
                            .nickname(nickname != null ? nickname : u.getNickname())
                            .profileImage(profile != null ? profile : u.getProfileImage())
                            .createdAt(u.getCreatedAt())
                            .updatedAt(u.getUpdatedAt())
                            .lastLoginAt(LocalDateTime.now())
                            .build();
                    return userLoginRepository.save(u);
                })
                .orElseGet(() -> userLoginRepository.save(
                        User.builder()
                                .kakaoId(kakaoId)
                                .email(email)
                                .nickname(nickname)
                                .profileImage(profile)
                                .lastLoginAt(LocalDateTime.now())
                                .build()
                ));

        // Security 쪽으로 전달할 사용자
        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                attrs,
                "id" // nameAttributeKey
        );
    }
}
