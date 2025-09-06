package com.cloudtone31.userplants.dto;

import com.cloudtone31.userplants.domain.UserPlants;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CompletedPlantResponseDto {
    private Long id;
    private String plantName;
    private LocalDateTime completedAt;

    public CompletedPlantResponseDto(UserPlants userPlants) {
        this.id = userPlants.getId();
        this.plantName = userPlants.getPlantName();
        this.completedAt = userPlants.getUpdatedAt();
    }
}