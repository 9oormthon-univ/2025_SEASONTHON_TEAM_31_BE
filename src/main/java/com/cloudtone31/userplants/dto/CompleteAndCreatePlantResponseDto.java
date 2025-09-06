package com.cloudtone31.userplants.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompleteAndCreatePlantResponseDto {
    private CompletedPlantResponseDto completedPlant;
    private NewPlantResponseDto newPlant;
    private String rewardMessage;
}