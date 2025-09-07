package com.cloudtone31.mission.dto;

import com.cloudtone31.userplants.domain.UserPlants;
import lombok.Getter;

@Getter
public class PlantGrowthDto {
    private int previousPercentage;
    private int newPercentage;
    private int growthIncrease;

    public PlantGrowthDto(UserPlants plant, int growthIncrease) {
        this.newPercentage = plant.getGrowthPercentage();
        this.growthIncrease = growthIncrease;
        this.previousPercentage = newPercentage - growthIncrease;
    }
}