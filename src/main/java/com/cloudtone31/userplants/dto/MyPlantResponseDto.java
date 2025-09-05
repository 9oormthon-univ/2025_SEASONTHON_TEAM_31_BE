package com.cloudtone31.userplants.dto;

import com.cloudtone31.userplants.domain.UserPlants;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyPlantResponseDto {
    private Long id;
    private Long userId;
    private String plantType;
    private String plantName;
    private int growthPercentage;
    private String currentImage;
    private String growthStage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MyPlantResponseDto(UserPlants userPlants) {
        this.id = userPlants.getId();
        this.userId = userPlants.getUser().getId();
        this.plantType = userPlants.getPlantType();
        this.plantName = userPlants.getPlantName();
        this.growthPercentage = userPlants.getGrowthPercentage();
        this.currentImage = userPlants.getPlantImage();
        this.growthStage = userPlants.getGrowthStage();
        this.createdAt = userPlants.getCreatedAt();
        this.updatedAt = userPlants.getUpdatedAt();
    }
}