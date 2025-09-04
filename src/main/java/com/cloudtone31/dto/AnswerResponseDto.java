package com.cloudtone31.dto;

import com.cloudtone31.domain.Answers;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AnswerResponseDto {
    private Long id;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("mission_id")
    private Long missionId;
    @JsonProperty("answer_content")
    private String answerContent;
    @JsonProperty("answered_at")
    private LocalDateTime answeredAt;
    @JsonProperty("plant_growth")
    private PlantGrowthDto plantGrowth;

    public AnswerResponseDto(Answers answer) {
        this.id = answer.getId();
        this.userId = answer.getUser().getId();
        this.missionId = answer.getMission().getId();
        this.answerContent = answer.getAnswerContent();
        this.answeredAt = answer.getAnsweredAt();
        this.plantGrowth = new PlantGrowthDto();
    }
}