package com.cloudtone31.mission.dto;

import com.cloudtone31.mission.domain.DailyCondition;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ConditionStatDto {
    private LocalDate date;
    private String condition;

    public ConditionStatDto(DailyCondition dailyCondition) {
        this.date = dailyCondition.getSelectedAt().toLocalDate();
        this.condition = dailyCondition.getCondition();
    }
}