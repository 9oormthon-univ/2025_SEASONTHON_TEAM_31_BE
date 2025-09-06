package com.cloudtone31.userplants.dto;

import com.cloudtone31.userplants.domain.UserPlants;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CompletedPlantDto {
    private Long id;
    private String plantType;
    private String plantName;
    private String finalImage;
    private LocalDateTime completedAt;

    public CompletedPlantDto(UserPlants userPlants) {
        this.id = userPlants.getId();
        this.plantType = userPlants.getPlantType();
        this.plantName = userPlants.getPlantName();
        this.finalImage = userPlants.getPlantImage();
        this.completedAt = userPlants.getUpdatedAt();
    }
}