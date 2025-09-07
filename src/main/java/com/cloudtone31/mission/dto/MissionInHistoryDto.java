package com.cloudtone31.mission.dto;

import com.cloudtone31.mission.domain.Missions;
import lombok.Getter;

@Getter
public class MissionInHistoryDto {
    private Long id;
    private String question;
    private String condition;

    public MissionInHistoryDto(Missions mission) {
        this.id = mission.getId();
        this.question = mission.getQuestion();
        this.condition = mission.getCondition();
    }
}