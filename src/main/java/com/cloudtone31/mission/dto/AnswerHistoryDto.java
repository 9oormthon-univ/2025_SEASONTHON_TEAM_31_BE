package com.cloudtone31.mission.dto;

import com.cloudtone31.mission.domain.Answers;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class AnswerHistoryDto {
    private Long id;
    private MissionInHistoryDto mission;
    private String answerContent;
    private LocalDateTime answeredAt;

    public AnswerHistoryDto(Answers answer) {
        this.id = answer.getId();
        this.mission = new MissionInHistoryDto(answer.getMission());
        this.answerContent = answer.getAnswerContent();
        this.answeredAt = answer.getAnsweredAt();
    }
}