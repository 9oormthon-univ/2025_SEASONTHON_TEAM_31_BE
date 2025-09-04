package com.cloudtone31.mission.controller;

import com.cloudtone31.mission.domain.DailyCondition;
import com.cloudtone31.mission.dto.ConditionRequestDto;
import com.cloudtone31.mission.dto.ConditionResponseDto;
import com.cloudtone31.mission.dto.ConditionStatsResponseDto;
import com.cloudtone31.mission.service.ConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/conditions")
@RequiredArgsConstructor
public class ConditionController {

    private final ConditionService conditionService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> registerCondition(@RequestBody ConditionRequestDto requestDto) {
        Long tempUserId = 1L; // 임시 유저 ID

        DailyCondition savedCondition = conditionService.createDailyCondition(tempUserId, requestDto.getCondition());

        ConditionResponseDto data = new ConditionResponseDto(savedCondition);
        Map<String, Object> response = Map.of(
                "success", true,
                "data", data,
                "message", "컨디션이 등록되었습니다."
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getConditionStatistics(
            @RequestParam(value = "period", defaultValue = "week") String period) {
        Long tempUserId = 1L; // 임시 유저 ID

        ConditionStatsResponseDto statsData = conditionService.getConditionStats(tempUserId, period);

        Map<String, Object> response = Map.of(
                "success", true,
                "data", statsData,
                "message", "컨디션 통계를 성공적으로 조회했습니다."
        );

        return ResponseEntity.ok(response);
    }
}