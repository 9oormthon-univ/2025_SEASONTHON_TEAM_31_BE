package com.cloudtone31.mission.dto;

import com.cloudtone31.mission.domain.Missions;
import lombok.Getter;
import java.util.List;

@Getter
public class MissionResponseDto {
    private Long id;
    private String question;
    private List<String> options;
    private String missionType;

    public MissionResponseDto(Missions mission) {
        this.id = mission.getId();
        this.question = mission.getQuestion();
        this.options = mission.getOptions();

        if (this.options != null && !this.options.isEmpty()) {
            this.missionType = "objective";
        } else {
            this.missionType = "subjective";
        }
    }
}