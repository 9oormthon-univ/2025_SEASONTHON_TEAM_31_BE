package com.cloudtone31.mission.controller;

import com.cloudtone31.global.api.ApiResponse;
import com.cloudtone31.mission.domain.Answers;
import com.cloudtone31.mission.domain.Missions;
import com.cloudtone31.mission.dto.AnswerResponseDto;
import com.cloudtone31.mission.dto.MissionRequestDto;
import com.cloudtone31.mission.dto.MissionResponseDto;
import com.cloudtone31.mission.service.MissionService;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import com.cloudtone31.userplants.domain.UserPlants;
import com.cloudtone31.userplants.service.PlantsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;
    private final PlantsService plantsService;
    private final UserLoginRepository userLoginRepository;

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

    @GetMapping
    public ResponseEntity<ApiResponse<List<MissionResponseDto>>> getMissionsByCondition(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam("condition") String condition) {

        String kakaoId = extractKakaoId(principal.getAttributes());
        User user = userLoginRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long userId = user.getId();

        List<Missions> dailyMissions = missionService.getDailyMissions(userId, condition);

        List<MissionResponseDto> responseDtos = dailyMissions.stream()
                .map(MissionResponseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(responseDtos, "미션을 성공적으로 조회했습니다."));
    }

    @PostMapping("/{missionId}/answers")
    public ResponseEntity<ApiResponse<AnswerResponseDto>> submitAnswer(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable("missionId") Long missionId,
            @RequestBody MissionRequestDto requestDto) {

        String kakaoId = extractKakaoId(principal.getAttributes());
        User user = userLoginRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long userId = user.getId();

        Answers savedAnswer = missionService.submitAnswer(userId, missionId, requestDto.getAnswerContent());
        UserPlants grownPlant = plantsService.getMyPlant(userId);

        AnswerResponseDto data = new AnswerResponseDto(savedAnswer, grownPlant);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(data, "답변이 제출되었습니다. 식물이 성장했습니다!"));
    }
}