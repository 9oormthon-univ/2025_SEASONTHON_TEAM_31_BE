package com.cloudtone31.user.controller;

import com.cloudtone31.global.api.ApiResponse;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.dto.NicknameReq;
import com.cloudtone31.user.dto.NicknameRes;
import com.cloudtone31.user.dto.UserMeDto;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserLoginRepository userLoginRepository;
    private final UserService userService;

    /** 현재 로그인한 사용자 정보 조회 : GET /users/me */
    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<?>> me(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.fail("인증이 필요합니다."));
        }

        String kakaoId = extractKakaoId(principal.getAttributes());
        if (kakaoId == null || kakaoId.isBlank()) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.fail("OAuth2 attributes에서 카카오 ID를 찾을 수 없습니다."));
        }

        User user = userLoginRepository.findByKakaoId(kakaoId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
        }

        return ResponseEntity.ok(ApiResponse.ok(UserMeDto.of(user), "사용자 정보를 성공적으로 조회했습니다."));
    }

    /** 회원탈퇴 : DELETE /users/delete */
    @DeleteMapping("/users/delete")
    public ResponseEntity<ApiResponse<?>> deleteMe(@AuthenticationPrincipal OAuth2User principal,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   Authentication authentication) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.fail("인증이 필요합니다."));
        }

        String kakaoId = extractKakaoId(principal.getAttributes());
        if (kakaoId == null || kakaoId.isBlank()) {
            return ResponseEntity.status(400).body(ApiResponse.fail("프로필 정보가 올바르지 않습니다."));
        }

        User user = userLoginRepository.findByKakaoId(kakaoId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
        }

        userLoginRepository.delete(user);

        // 로그아웃 + 쿠키 만료
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        ResponseCookie expired = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expired.toString());

        return ResponseEntity.ok(ApiResponse.ok(null, "회원탈퇴가 완료되었습니다"));
    }

    /** 닉네임 변경 : PUT /v1/auth/kakao/nickname  */
    @PutMapping("/v1/auth/kakao/nickname")
    public ResponseEntity<ApiResponse<?>> updateNickname(@AuthenticationPrincipal OAuth2User principal,
                                                         @Valid @org.springframework.web.bind.annotation.RequestBody NicknameReq req) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.fail("인증이 필요합니다."));
        }

        String kakaoId = extractKakaoId(principal.getAttributes());
        if (kakaoId == null || kakaoId.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("프로필 정보가 올바르지 않습니다."));
        }

        // ⚠️ userService의 시그니처도 String kakaoId 로 변경해야 함
        User updated = userService.updateNickname(kakaoId, req.getNickname());
        NicknameRes body = new NicknameRes(updated.getId(), updated.getNickname());
        return ResponseEntity.ok(ApiResponse.ok(body, "닉네임이 변경되었습니다."));
    }

    /** OAuth2 attributes에서 Kakao ID를 문자열로 추출 */
    private String extractKakaoId(Map<String, Object> attributes) {
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