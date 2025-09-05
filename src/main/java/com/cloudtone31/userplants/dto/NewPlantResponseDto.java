package com.cloudtone31.userplants.dto;

import com.cloudtone31.userplants.domain.UserPlants;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class NewPlantResponseDto {
    private Long id;
    private String plantType;
    private String plantName;
    private int growthPercentage;
    private String currentImage;
    private String growthStage;
    private LocalDateTime createdAt;

    public NewPlantResponseDto(UserPlants userPlants) {
        this.id = userPlants.getId();
        this.plantType = userPlants.getPlantType();
        this.plantName = userPlants.getPlantName();
        this.growthPercentage = userPlants.getGrowthPercentage();
        this.currentImage = userPlants.getPlantImage();
        this.growthStage = userPlants.getGrowthStage();
        this.createdAt = userPlants.getCreatedAt();
    }
}