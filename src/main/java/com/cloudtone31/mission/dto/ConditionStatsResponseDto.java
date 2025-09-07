package com.cloudtone31.mission.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ConditionStatsResponseDto {
    private String period;
    private List<ConditionStatDto> statistics;
    private Map<String, Long> summary;

    public ConditionStatsResponseDto(String period, List<ConditionStatDto> statistics, Map<String, Long> summary) {
        this.period = period;
        this.statistics = statistics;
        this.summary = summary;
    }
}