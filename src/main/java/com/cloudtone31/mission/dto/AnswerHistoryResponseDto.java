package com.cloudtone31.mission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class AnswerHistoryResponseDto {
    private List<AnswerHistoryDto> answers;
    private PaginationDto pagination;
}