// src/main/java/com/cloudtone31/global/config/OAuth2SuccessHandler.java
package com.cloudtone31.global.config;

import com.cloudtone31.auth.service.OneTimeCodeService;
import com.cloudtone31.user.domain.User;

import com.cloudtone31.user.repository.UserLoginRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OneTimeCodeService codeService;   // (Redis 기반 원타임 코드)
    private final UserLoginRepository userLoginRepository;    // users 조회/생성에 사용

    // 프론트 콜백 주소 (모바일 딥링크 or 웹 URL)
    @Value("${app.oauth2.success-redirect:exp://192.168.2.98:8081/--/auth/callback}")
    private String successRedirect;

    // OTC TTL (초) - 기본 120초
    @Value("${app.oauth2.otc-ttl-seconds:120}")
    private long otcTtlSeconds;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth) throws IOException {
        OAuth2User principal = (OAuth2User) auth.getPrincipal();

        // 1) 카카오 프로필에서 우리 서비스의 userId 식별/생성
        String userId = resolveUserId(principal);

        // 2) 1회용 코드 발급 (TTL 적용)
        String code = codeService.issue(userId, Duration.ofSeconds(otcTtlSeconds));

        // 3) 프론트 콜백으로 리다이렉트 (?code=...)
        String redirect = UriComponentsBuilder.fromUriString(successRedirect)
                .queryParam("code", code)
                .build(true)
                .toUriString();

        getRedirectStrategy().sendRedirect(req, res, redirect);
    }

    /**
     * 카카오 OAuth2User에서 우리 서비스의 userId를 결정.
     *  - users.kakao_id로 조회
     *  - 없으면 생성 후 PK 반환
     */
    private String resolveUserId(OAuth2User principal) {
        Map<String, Object> attrs = principal.getAttributes();
        String kakaoId = String.valueOf(attrs.get("id"));

        String nickname = extractNickname(attrs);
        String name = extractName(attrs, nickname);
        String profileImage = extractProfileImage(attrs);
        String email = extractEmail(attrs);

        Optional<User> existing = userLoginRepository.findByKakaoId(kakaoId);
        if (existing.isPresent()) {
            User u = existing.get();
            // 편의 메서드 사용
            u.updateLastLoginAt(LocalDateTime.now());
            if (nickname != null) u.updateNickname(nickname);
            if (name != null) u.setName(name);              // ⚠️ setName 없음 → 추가 필요
            if (profileImage != null) u.setProfileImage(profileImage); // ⚠️ 없음 → 추가 필요
            if (email != null) u.setEmail(email);           // ⚠️ 없음 → 추가 필요

            userLoginRepository.save(u);
            return String.valueOf(u.getId());
        }

        User newUser = User.builder()
                .kakaoId(kakaoId)
                .name(name != null ? name : "사용자")
                .nickname(nickname != null ? nickname : "새싹")
                .profileImage(profileImage)
                .email(email)
                .lastLoginAt(LocalDateTime.now())
                .build();

        userLoginRepository.save(newUser);  // ✅ 저장
        return String.valueOf(newUser.getId());
    }

    private String extractName(Map<String, Object> attrs, String fallback) {
        if (fallback != null) return fallback;
        Object accountObj = attrs.get("kakao_account");
        if (accountObj instanceof Map<?, ?> account) {
            Object profileObj = account.get("profile");
            if (profileObj instanceof Map<?, ?> profile) {
                Object nameObj = profile.get("name");
                if (nameObj != null) return String.valueOf(nameObj);
            }
        }
        return null;
    }

    private String extractNickname(Map<String, Object> attrs) {
        Object accountObj = attrs.get("kakao_account");
        if (accountObj instanceof Map<?, ?> account) {
            Object profileObj = account.get("profile");
            if (profileObj instanceof Map<?, ?> profile) {
                Object nickObj = profile.get("nickname");
                if (nickObj != null) return String.valueOf(nickObj);
            }
        }
        return null;
    }

    private String extractProfileImage(Map<String, Object> attrs) {
        Object accountObj = attrs.get("kakao_account");
        if (accountObj instanceof Map<?, ?> account) {
            Object profileObj = account.get("profile");
            if (profileObj instanceof Map<?, ?> profile) {
                Object img = profile.get("profile_image_url");
                if (img != null) return String.valueOf(img);
            }
        }
        return null;
    }

    private String extractEmail(Map<String, Object> attrs) {
        Object accountObj = attrs.get("kakao_account");
        if (accountObj instanceof Map<?, ?> account) {
            Object emailObj = account.get("email");
            if (emailObj != null) return String.valueOf(emailObj);
        }
        return null;
    }
}