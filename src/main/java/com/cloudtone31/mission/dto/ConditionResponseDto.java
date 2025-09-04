package com.cloudtone31.mission.dto;

import com.cloudtone31.mission.domain.DailyCondition;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ConditionResponseDto {
    private Long id;
    @JsonProperty("user_id")
    private Long userId;
    private String condition;
    private LocalDateTime selectedAt;

    public ConditionResponseDto(DailyCondition dailyCondition) {
        this.id = dailyCondition.getId();
        this.userId = dailyCondition.getUser().getId();
        this.condition = dailyCondition.getCondition();
        this.selectedAt = dailyCondition.getSelectedAt();
    }
}