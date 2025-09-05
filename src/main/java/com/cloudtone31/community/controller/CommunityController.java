package com.cloudtone31.community.controller;

import com.cloudtone31.community.domain.Community;
import com.cloudtone31.community.dto.CommunityRequestDTO;
import com.cloudtone31.community.dto.CommunityResponseDTO;
import com.cloudtone31.community.service.CommunityService;
import com.cloudtone31.global.api.ApiResponse;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {
    private static final Logger log = LoggerFactory.getLogger(CommunityController.class);

    private final CommunityService communityService;
    private final UserLoginRepository userLoginRepository;

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody CommunityRequestDTO requestDTO, @AuthenticationPrincipal OAuth2User principal) {

        // ---▼▼▼ 임시 디버깅 코드 ▼▼▼---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.error("### SecurityContext에 Authentication 객체가 없습니다. (null)");
        } else {
            log.info("### 현재 Authentication: {}", authentication);
            log.info("### Principal 타입: {}", authentication.getPrincipal().getClass().getName());
        }
        // ---▲▲▲ 임시 디버깅 코드 ▲▲▲---

        if (principal == null) {
            log.error("### @AuthenticationPrincipal principal 객체가 null 입니다.");
            // This line no longer causes a compile error due to the changed method signature
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail("인증 정보가 없습니다."));
        }

        String kakaoId = extractKakaoId(principal.getAttributes());
        User user = userLoginRepository.findByKakaoId(kakaoId).orElse(null);
        Long userId = user.getId();
        Community community = communityService.create(requestDTO, userId);
        CommunityResponseDTO communityResponseDTO = CommunityResponseDTO.from(community);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(communityResponseDTO, "게시물이 작성되었습니다."));

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
