package com.cloudtone31.community.controller;

import com.cloudtone31.community.domain.Community;
import com.cloudtone31.community.dto.CommunityListResponseDTO;
import com.cloudtone31.community.dto.CommunityRequestDTO;
import com.cloudtone31.community.dto.CommunityResponseDTO;
import com.cloudtone31.community.service.CommunityService;
import com.cloudtone31.global.api.ApiResponse;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;
    private final UserLoginRepository userLoginRepository;

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody CommunityRequestDTO requestDTO, @AuthenticationPrincipal OAuth2User principal) {

        String kakaoId = extractKakaoId(principal.getAttributes());
        User user = userLoginRepository.findByKakaoId(kakaoId).orElse(null);
        Long userId = user.getId();
        Community community = communityService.create(requestDTO, userId);
        CommunityResponseDTO communityResponseDTO = CommunityResponseDTO.from(community);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(communityResponseDTO, "게시물이 작성되었습니다."));

    }

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<CommunityListResponseDTO>> getPostsList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "sort", defaultValue = "latest") String sort
    ){
        CommunityListResponseDTO data = communityService.getCommunityList(page, limit, sort);
        return ResponseEntity.ok(ApiResponse.ok(data, "게시물 목록이 성공적으로 조회되었습니다."));
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
