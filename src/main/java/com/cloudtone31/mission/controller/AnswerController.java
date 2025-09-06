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

    /** 답변 히스토리 조회 */
    @GetMapping("/history")
    public ResponseEntity<?> getAnswerHistory(
            @LoginUser String principal,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "date_from", required = false) String dateFrom,
            @RequestParam(value = "date_to", required = false) String dateTo
    ) {
        if (page < 1) page = 1;
        if (limit < 1) limit = 20;
        if (limit > 100) limit = 100;

        Long userId = resolveUserIdFlexible(principal);

        AnswerHistoryResponseDto history =
                answerService.getAnswerHistory(userId, page, limit, dateFrom, dateTo);

        return ResponseEntity.ok(
                ApiResponse.ok(history, "답변 히스토리를 성공적으로 조회했습니다.")
        );
    }

    /** "3" 같은 숫자면 userId, 그 외면 kakaoId로 조회 */
    private Long resolveUserIdFlexible(String principal) {
        if (principal == null || principal.isBlank()) {
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }
        boolean numeric = principal.chars().allMatch(Character::isDigit);
        if (numeric) return Long.parseLong(principal);

        return userLoginRepository.findByKakaoId(principal)
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}