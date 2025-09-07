package com.cloudtone31.userplants.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class PlantCollectionResponseDto {
    private CurrentPlantDto currentPlant;
    private List<CompletedPlantDto> completedPlants;
    private Map<String, Integer> collectionStats;
}