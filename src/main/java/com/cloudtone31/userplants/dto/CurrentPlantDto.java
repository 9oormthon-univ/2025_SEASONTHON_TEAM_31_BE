package com.cloudtone31.userplants.dto;

import com.cloudtone31.userplants.domain.UserPlants;
import lombok.Getter;

@Getter
public class CurrentPlantDto {
    private Long id;
    private String plantType;
    private String plantName;
    private int growthPercentage;
    private String currentImage;

    public CurrentPlantDto(UserPlants userPlants) {
        this.id = userPlants.getId();
        this.plantType = userPlants.getPlantType();
        this.plantName = userPlants.getPlantName();
        this.growthPercentage = userPlants.getGrowthPercentage();
        this.currentImage = userPlants.getPlantImage();
    }
}