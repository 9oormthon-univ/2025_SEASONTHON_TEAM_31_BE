package com.cloudtone31.userplants.service;

import com.cloudtone31.userplants.domain.PlantType;
import com.cloudtone31.userplants.domain.UserPlants;
import com.cloudtone31.userplants.dto.*;
import com.cloudtone31.userplants.repository.PlantTypeRepository;
import com.cloudtone31.userplants.repository.UserPlantsRepository;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlantsService {

    private final UserPlantsRepository userPlantsRepository;
    private final UserLoginRepository userLoginRepository;
    private final PlantTypeRepository plantTypeRepository;

    @Transactional(readOnly = true)
    public UserPlants getMyPlant(Long userId) {
        return userPlantsRepository.findByUserIdAndIsActive(userId, true)
                .orElseThrow(() -> new IllegalArgumentException("키우고 있는 식물을 찾을 수 없습니다."));
    }


    @Transactional
    public UserPlants updatePlantName(Long userId, Long plantId, String newName) {

        UserPlants userPlants = userPlantsRepository.findById(plantId)
                .orElseThrow(() -> new IllegalArgumentException("해당 식물을 찾을 수 없습니다."));

        if (!userPlants.getUser().getId().equals(userId)) {
            throw new SecurityException("식물 정보를 수정할 권한이 없습니다.");
        }

        userPlants.updatePlantName(newName);

        return userPlants;
    }


    @Transactional(readOnly = true)
    public PlantCollectionResponseDto getPlantCollection(Long userId) {
        List<UserPlants> allPlants = userPlantsRepository.findAllByUserId(userId);

        UserPlants currentPlant = allPlants.stream()
                .filter(p -> p.isActive())
                .findFirst()
                .orElse(null);

        List<UserPlants> completedPlantsList = allPlants.stream()
                .filter(p -> !p.isActive())
                .collect(Collectors.toList());

        CurrentPlantDto currentPlantDto = (currentPlant != null) ? new CurrentPlantDto(currentPlant) : null;

        List<CompletedPlantDto> completedPlantDtos = completedPlantsList.stream()
                .map(CompletedPlantDto::new)
                .collect(Collectors.toList());

        Map<String, Integer> stats = Map.of(
                "total_plants", allPlants.size(),
                "completed_plants", completedPlantsList.size()
        );

        return new PlantCollectionResponseDto(currentPlantDto, completedPlantDtos, stats);
    }


    @Transactional
    public CompleteAndCreatePlantResponseDto completeAndCreateNewPlant(Long userId) {

        UserPlants currentPlant = userPlantsRepository.findByUserIdAndIsActive(userId, true)
                .orElseThrow(() -> new IllegalArgumentException("키우고 있는 식물이 없습니다."));

        if (currentPlant.getGrowthPercentage() < 100) {
            throw new IllegalStateException("식물이 아직 100% 성장하지 않았습니다.");
        }

        currentPlant.complete();
        CompletedPlantResponseDto completedDto = new CompletedPlantResponseDto(currentPlant);

        List<PlantType> plantOrder = plantTypeRepository.findAllByOrderByUnlockOrderAsc();
        if (plantOrder.isEmpty()) {
            throw new IllegalStateException("새로 지급할 식물 정보가 없습니다.");
        }

        long completedCount = userPlantsRepository.findAllByUserId(userId).stream().filter(p -> !p.isActive()).count();

        int nextPlantIndex = (int) (completedCount % plantOrder.size());
        PlantType nextPlantInfo = plantOrder.get(nextPlantIndex);

        String newPlantType = nextPlantInfo.getTypeCode();
        String newPlantName = "나의 " + nextPlantInfo.getName();

        User user = userLoginRepository.findById(userId).get();
        UserPlants newPlant = UserPlants.builder()
                .user(user)
                .plantType(newPlantType)
                .plantName(newPlantName)
                .growthPercentage(0)
                .plantImage(String.format("/images/%s_stage0.png", newPlantType))
                .growthStage("씨앗")
                .isActive(true)
                .build();

        userPlantsRepository.save(newPlant);
        NewPlantResponseDto newPlantDto = new NewPlantResponseDto(newPlant);

        String message = String.format("축하합니다! %s를 완성하고 새로운 %s를 획득했습니다!",
                currentPlant.getPlantName(), newPlant.getPlantName());

        return new CompleteAndCreatePlantResponseDto(completedDto, newPlantDto, message);
    }

    @Transactional
    public UserPlants growPlant(Long userId) {
        UserPlants myPlant = getMyPlant(userId);

        int newPercentage = Math.min(100, myPlant.getGrowthPercentage() + 5);
        myPlant.setGrowthPercentage(newPercentage);

        if (newPercentage >= 75) {
            myPlant.updateGrowthStage("꽃");
        } else if (newPercentage >= 25) {
            myPlant.updateGrowthStage("줄기");
        }

        return myPlant;
    }
}