package com.cloudtone31.mission.controller;

import com.cloudtone31.auth.LoginUser;
import com.cloudtone31.global.api.ApiResponse;
import com.cloudtone31.mission.domain.DailyCondition;
import com.cloudtone31.mission.dto.ConditionRequestDto;
import com.cloudtone31.mission.dto.ConditionResponseDto;
import com.cloudtone31.mission.dto.ConditionStatsResponseDto;
import com.cloudtone31.mission.service.ConditionService;
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
@RequestMapping("/conditions")
@RequiredArgsConstructor
public class ConditionController {

    private final ConditionService conditionService;
    private final UserLoginRepository userLoginRepository;


    @PostMapping
    public ResponseEntity<ApiResponse<ConditionResponseDto>> registerCondition(
            @LoginUser String kakaoId,
            @RequestBody ConditionRequestDto requestDto) {

        User user = userLoginRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long userId = user.getId();

        DailyCondition savedCondition = conditionService.createDailyCondition(userId, requestDto.getCondition());

        ConditionResponseDto data = new ConditionResponseDto(savedCondition);

        return ResponseEntity.ok(ApiResponse.ok(data, "컨디션이 등록되었습니다."));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<ConditionStatsResponseDto>> getConditionStatistics(
            @LoginUser String kakaoId,
            @RequestParam(value = "period", defaultValue = "week") String period) {

        User user = userLoginRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long userId = user.getId();

        ConditionStatsResponseDto statsData = conditionService.getConditionStats(userId, period);

        return ResponseEntity.ok(ApiResponse.ok(statsData, "컨디션 통계를 성공적으로 조회했습니다."));
    }
}