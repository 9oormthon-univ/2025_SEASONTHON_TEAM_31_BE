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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plants")
@RequiredArgsConstructor
public class PlantsController {

    private final PlantsService plantsService;
    private final UserLoginRepository userLoginRepository;


    // [GET] /plants/my
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<?>> getMyPlant(@LoginUser String kakaoId) {

        User user = userLoginRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long userId = user.getId();

        UserPlants myPlant = plantsService.getMyPlant(userId);
        MyPlantResponseDto responseDto = new MyPlantResponseDto(myPlant);

        return ResponseEntity.ok(ApiResponse.ok(responseDto, "식물 정보를 성공적으로 조회했습니다."));
    }

    // [PUT] /plants/{plantId}/name
    @PutMapping("/{plantId}/name")
    public ResponseEntity<ApiResponse<?>> updatePlantName(
            @LoginUser String kakaoId,
            @PathVariable("plantId") Long plantId,
            @RequestBody UpdatePlantNameRequestDto requestDto) {

        User user = userLoginRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long userId = user.getId();

        plantsService.updatePlantName(userId, plantId, requestDto.getPlantName());

        Map<String, Object> responseData = Map.of(
                "id", plantId,
                "plant_name", requestDto.getPlantName()
        );

        return ResponseEntity.ok(ApiResponse.ok(responseData, "식물 이름이 수정되었습니다."));
    }

    // [GET] /plants/collection
    @GetMapping("/collection")
    public ResponseEntity<ApiResponse<?>> getPlantCollection(@LoginUser String kakaoId) {

        User user = userLoginRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long userId = user.getId();

        PlantCollectionResponseDto responseDto = plantsService.getPlantCollection(userId);

        return ResponseEntity.ok(ApiResponse.ok(responseDto, "식물 컬렉션을 성공적으로 조회했습니다."));
    }

    // [POST] /plants/complete
    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<?>> completePlant(@LoginUser String kakaoId) {

        User user = userLoginRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long userId = user.getId();

        CompleteAndCreatePlantResponseDto responseDto = plantsService.completeAndCreateNewPlant(userId);

        return ResponseEntity.ok(ApiResponse.ok(responseDto, "식물이 완성되어 새로운 식물을 시작합니다!"));
    }
}