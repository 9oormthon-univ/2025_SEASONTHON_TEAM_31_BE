package com.cloudtone31.mission.controller;

import com.cloudtone31.global.api.ApiResponse;
import com.cloudtone31.mission.dto.AnswerHistoryResponseDto;
import com.cloudtone31.mission.service.AnswerService;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;
    private final UserLoginRepository userLoginRepository;

    private String extractKakaoId(Map<String, Object> attributes) {
        // (MissionController에서 사용했던 헬퍼 메소드 그대로 복사)
        for (String key : List.of("kakaoId", "kakao_id", "id", "sub")) {
            Object v = attributes.get(key);
            if (v == null) continue;
            if (v instanceof String s && !s.isBlank()) return s;
            if (v instanceof Number n) return String.valueOf(n.longValue());
            return String.valueOf(v);
        }
        return null;
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<AnswerHistoryResponseDto>> getAnswerHistory(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "date_from", required = false) String dateFrom,
            @RequestParam(value = "date_to", required = false) String dateTo) {

        String kakaoId = extractKakaoId(principal.getAttributes());
        User user = userLoginRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long userId = user.getId();

        AnswerHistoryResponseDto historyData = answerService.getAnswerHistory(userId, page, limit, dateFrom, dateTo);

        return ResponseEntity.ok(ApiResponse.ok(historyData, "답변 히스토리를 성공적으로 조회했습니다."));
    }
}