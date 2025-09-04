package com.cloudtone31.dto;

import lombok.Getter;

@Getter
public class PlantGrowthDto {
    private int previousPercentage;
    private int newPercentage;
    private int growthIncrease;

    // 식물 성장 로직 구현 전 임시 생성자
    public PlantGrowthDto() {
        this.previousPercentage = 45; // 임시 값
        this.newPercentage = 50;      // 임시 값
        this.growthIncrease = 5;      // 임시 값
    }
}