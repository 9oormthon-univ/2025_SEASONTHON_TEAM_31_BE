package com.cloudtone31.user.controller;

import com.cloudtone31.global.api.ApiResponse;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.dto.NicknameReq;
import com.cloudtone31.user.dto.NicknameRes;
import com.cloudtone31.user.repository.UserLoginRepository;
import com.cloudtone31.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserLoginRepository userLoginRepository;
    private final UserService userService;

    /** 현재 로그인한 사용자 정보 조회 : GET /users/me */
    @GetMapping("/users/me")
    public ResponseEntity<?> me(Authentication authentication) {
        Long userId = resolveUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
        }

        return userLoginRepository.findById(userId)
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of(
                        "id", u.getId(),
                        "kakaoId", u.getKakaoId(),
                        "name", u.getName(),
                        "nickname", u.getNickname(),
                        "email", u.getEmail(),
                        "profileImage", u.getProfileImage()
                )))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "사용자를 찾을 수 없습니다.")));
    }

    /** 회원탈퇴 : DELETE /users/delete */
    @DeleteMapping("/users/delete")
    public ResponseEntity<ApiResponse<?>> deleteMe(Authentication authentication,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {
        String kakaoId = resolveKakaoId(authentication);
        if (kakaoId == null) {
            return ResponseEntity.status(401).body(ApiResponse.fail("인증이 필요합니다."));
        }

        User user = userLoginRepository.findByKakaoId(kakaoId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
        }

        userLoginRepository.delete(user);

        // 세션 기반일 때만 세션/쿠키 정리 (JWT 전용 클라이언트는 토큰 폐기로 충분)
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        ResponseCookie expired = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expired.toString());

        return ResponseEntity.ok(ApiResponse.ok(null, "회원탈퇴가 완료되었습니다"));
    }

    /** 닉네임 변경 : PUT /v1/auth/kakao/nickname */
    @PutMapping("/v1/auth/kakao/nickname")
    public ResponseEntity<ApiResponse<?>> updateNickname(Authentication authentication,
                                                         @Valid @RequestBody NicknameReq req) {
        Long userId = resolveUserId(authentication); // userId 계산 (JWT or OAuth2 -> DB 조회)
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.fail("인증이 필요합니다."));
        }

        User updated = userService.updateNicknameByUserId(userId, req.getNickname());
        NicknameRes body = new NicknameRes(updated.getId(), updated.getNickname());
        return ResponseEntity.ok(ApiResponse.ok(body, "닉네임이 변경되었습니다."));
    }

    /* ===================== 헬퍼 ===================== */

    /** JWT(Principal=String userId) & OAuth2 세션(OAuth2User) 모두 지원 */
    private Long resolveUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        Object principal = authentication.getPrincipal();

        // 1) JWT: principal이 사용자 PK(String) 인 경우
        if (principal instanceof String s && !s.isBlank()) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // 2) 세션(OAuth2): kakaoId → DB 조회로 userId 반환
        if (principal instanceof OAuth2User o) {
            String kakaoId = extractKakaoIdFromAttributes(o.getAttributes());
            if (kakaoId == null) return null;
            return userLoginRepository.findByKakaoId(kakaoId)
                    .map(User::getId)
                    .orElse(null);
        }

        return null;
    }

    /** Authentication에서 kakaoId 추출 (주로 세션 로그인용) */
    private String resolveKakaoId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;

        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User o) {
            return extractKakaoIdFromAttributes(o.getAttributes());
        }
        // 필요 시 JWT의 principal이 kakaoId인 케이스 추가 가능

        return null;
    }

    /** OAuth2 attributes에서 Kakao ID 추출 */
    private String extractKakaoIdFromAttributes(Map<String, Object> attributes) {
        for (String key : List.of("kakaoId", "kakao_id", "id", "sub")) {
            Object v = attributes.get(key);
            if (v == null) continue;
            if (v instanceof String s && !s.isBlank()) return s;
            if (v instanceof Number n) return String.valueOf(n.longValue());
            return String.valueOf(v);
        }
        return null;
    }
}