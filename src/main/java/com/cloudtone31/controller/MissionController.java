package com.cloudtone31.controller;

import com.cloudtone31.domain.Answers;
import com.cloudtone31.domain.Missions;
import com.cloudtone31.dto.AnswerResponseDto;
import com.cloudtone31.dto.MissionRequestDto;
import com.cloudtone31.dto.MissionResponseDto;
import com.cloudtone31.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @GetMapping
    public ResponseEntity<List<MissionResponseDto>> getMissionsByCondition(@RequestParam("condition") String condition) {
        Long tempUserId = 1L;
        List<Missions> dailyMissions = missionService.getDailyMissions(tempUserId, condition);

        List<MissionResponseDto> responseDtos = dailyMissions.stream()
                .map(MissionResponseDto::new) // 이 부분이 간단해야 합니다.
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    @PostMapping("/{missionId}/answers")
    public ResponseEntity<Map<String, Object>> submitAnswer( // 반환 타입 수정
             @PathVariable("missionId") Long missionId,
             @RequestBody MissionRequestDto requestDto) {

        Long tempUserId = 1L;
        Answers savedAnswer = missionService.submitAnswer(tempUserId, missionId, requestDto.getAnswerContent());

        AnswerResponseDto data = new AnswerResponseDto(savedAnswer);

        Map<String, Object> response = Map.of(
                "success", true,
                "data", data,
                "message", "답변이 제출되었습니다. 식물이 성장했습니다!"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}