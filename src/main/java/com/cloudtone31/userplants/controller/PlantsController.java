package com.cloudtone31.userplants.controller;

import com.cloudtone31.auth.LoginUser;
import com.cloudtone31.global.api.ApiResponse;
import com.cloudtone31.userplants.domain.UserPlants;
import com.cloudtone31.userplants.dto.CompleteAndCreatePlantResponseDto;
import com.cloudtone31.userplants.dto.MyPlantResponseDto;
import com.cloudtone31.userplants.dto.PlantCollectionResponseDto;
import com.cloudtone31.userplants.dto.UpdatePlantNameRequestDto;
import com.cloudtone31.userplants.service.PlantsService;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/plants")
@RequiredArgsConstructor
public class PlantsController {

    private final PlantsService plantsService;
    private final UserLoginRepository userLoginRepository;

    /** [GET] /plants/my */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<?>> getMyPlant(@LoginUser String userIdStr) {
        // 1) 인증 확인
        Long userId = parseUserIdOr401(userIdStr);

        // 2) 사용자 존재 확인
        User user = userLoginRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
        }

        // 3) 내 식물 조회 (null 가능 시 null-safe 처리)
        UserPlants myPlant = plantsService.getMyPlant(userId);
        MyPlantResponseDto responseDto = new MyPlantResponseDto(myPlant);

        return ResponseEntity.ok(ApiResponse.ok(responseDto, "식물 정보를 성공적으로 조회했습니다."));
    }

    /** [PUT] /plants/{plantId}/name */
    @PutMapping("/{plantId}/name")
    public ResponseEntity<ApiResponse<?>> updatePlantName(
            @LoginUser String userIdStr,
            @PathVariable("plantId") Long plantId,
            @RequestBody UpdatePlantNameRequestDto requestDto) {

        Long userId = parseUserIdOr401(userIdStr);

        User user = userLoginRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
        }

        if (requestDto == null || !StringUtils.hasText(requestDto.getPlantName())) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("plantName은 비어 있을 수 없습니다."));
        }

        plantsService.updatePlantName(userId, plantId, requestDto.getPlantName());

        Map<String, Object> data = Map.of(
                "id", plantId,
                "plant_name", requestDto.getPlantName()
        );
        return ResponseEntity.ok(ApiResponse.ok(data, "식물 이름이 수정되었습니다."));
    }

    /** [GET] /plants/collection */
    @GetMapping("/collection")
    public ResponseEntity<ApiResponse<?>> getPlantCollection(@LoginUser String userIdStr) {
        Long userId = parseUserIdOr401(userIdStr);

        User user = userLoginRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
        }

        PlantCollectionResponseDto responseDto = plantsService.getPlantCollection(userId);
        return ResponseEntity.ok(ApiResponse.ok(responseDto, "식물 컬렉션을 성공적으로 조회했습니다."));
    }

    /** [POST] /plants/complete */
    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<?>> completePlant(@LoginUser String userIdStr) {
        Long userId = parseUserIdOr401(userIdStr);

        User user = userLoginRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
        }

        CompleteAndCreatePlantResponseDto responseDto = plantsService.completeAndCreateNewPlant(userId);
        return ResponseEntity.ok(ApiResponse.ok(responseDto, "식물이 완성되어 새로운 식물을 시작합니다!"));
    }

    /** userId 문자열을 Long으로 파싱, 실패 시 401 반환용 헬퍼 */
    private Long parseUserIdOr401(String userIdStr) {
        if (!StringUtils.hasText(userIdStr)) {
            throw new UnauthorizedException("인증이 필요합니다."); // GlobalExceptionHandler에서 401 매핑 가정
        }
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("잘못된 인증 주체(sub) 형식입니다.");
        }
    }

    // 간단한 401용 런타임 예외 (프로젝트의 공통 예외가 있다면 그걸 사용)
    static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) { super(message); }
    }
}