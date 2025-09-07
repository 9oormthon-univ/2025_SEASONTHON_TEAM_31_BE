package com.cloudtone31.mission.controller;

import com.cloudtone31.auth.LoginUser;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;
    private final PlantsService plantsService;
    private final UserLoginRepository userLoginRepository;

    /** 오늘의 미션 조회 (조건 기반) */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MissionResponseDto>>> getMissionsByCondition(
            @LoginUser String principal,
            @RequestParam("condition") String condition) {

        if (!StringUtils.hasText(condition)) {
            throw new IllegalArgumentException("condition 파라미터가 필요합니다.");
        }

        Long userId = resolveUserIdFlexible(principal);

        List<Missions> dailyMissions = missionService.getDailyMissions(userId, condition);
        List<MissionResponseDto> body = dailyMissions.stream()
                .map(MissionResponseDto::new)
                .toList();

        return ResponseEntity.ok(ApiResponse.ok(body, "미션을 성공적으로 조회했습니다."));
    }

    /** 미션 답변 제출 + 식물 성장 반영 */
    @PostMapping("/{missionId}/answers")
    public ResponseEntity<ApiResponse<AnswerResponseDto>> submitAnswer(
            @LoginUser String principal,
            @PathVariable Long missionId,
            @RequestBody MissionRequestDto requestDto) {

        if (requestDto == null || !StringUtils.hasText(requestDto.getAnswerContent())) {
            throw new IllegalArgumentException("answerContent가 비어있습니다.");
        }

        Long userId = resolveUserIdFlexible(principal);

        Answers savedAnswer = missionService.submitAnswer(userId, missionId, requestDto.getAnswerContent());
        UserPlants grownPlant = plantsService.getMyPlant(userId); // 없으면 null 가능

        AnswerResponseDto data = new AnswerResponseDto(savedAnswer, grownPlant);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(data, "답변이 제출되었습니다. 식물이 성장했습니다!"));
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