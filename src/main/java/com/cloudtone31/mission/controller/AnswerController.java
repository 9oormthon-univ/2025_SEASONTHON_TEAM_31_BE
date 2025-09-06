package com.cloudtone31.mission.controller;

import com.cloudtone31.auth.LoginUser;
import com.cloudtone31.global.api.ApiResponse;
import com.cloudtone31.mission.dto.AnswerHistoryResponseDto;
import com.cloudtone31.mission.service.AnswerService;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;
    private final UserLoginRepository userLoginRepository;

    /**
     * 답변 히스토리 조회
     * GET /answers/history?page=1&limit=20&date_from=2025-09-01&date_to=2025-09-07
     */
    @GetMapping("/history")
    public ResponseEntity<?> getAnswerHistory(
            @LoginUser String kakaoId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "date_from", required = false) String dateFrom,
            @RequestParam(value = "date_to", required = false) String dateTo
    ) {
        // 1) 인증 확인
        if (kakaoId == null || kakaoId.isBlank()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.fail("인증이 필요합니다."));
        }

        // 2) 파라미터 가드
        if (page < 1) page = 1;
        if (limit < 1) limit = 20;
        if (limit > 100) limit = 100;

        // 3) 사용자 조회 (NPE 방지)
        User user = userLoginRepository.findByKakaoId(kakaoId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
        }
        Long userId = user.getId();

        // 4) 서비스 호출 (서비스 시그니처 그대로: String dateFrom/dateTo)
        AnswerHistoryResponseDto historyData =
                answerService.getAnswerHistory(userId, page, limit, dateFrom, dateTo);

        return ResponseEntity.ok(
                ApiResponse.ok(historyData, "답변 히스토리를 성공적으로 조회했습니다.")
        );
    }
}