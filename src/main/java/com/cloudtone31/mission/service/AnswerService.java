package com.cloudtone31.mission.service;

import com.cloudtone31.mission.domain.Answers;
import com.cloudtone31.mission.dto.*;
import com.cloudtone31.mission.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    @Transactional(readOnly = true)
    public AnswerHistoryResponseDto getAnswerHistory(Long userId, int page, int limit, String dateFrom, String dateTo) {
        Pageable pageable = PageRequest.of(page - 1, limit);

        LocalDateTime startDateTime = (dateFrom != null) ? LocalDate.parse(dateFrom).atStartOfDay() : null;
        LocalDateTime endDateTime = (dateTo != null) ? LocalDate.parse(dateTo).atTime(LocalTime.MAX) : null;

        Page<Answers> answerPage = answerRepository.findByUserIdWithFilters(userId, startDateTime, endDateTime, pageable);

        List<AnswerHistoryDto> answerDtos = answerPage.getContent().stream()
                .map(AnswerHistoryDto::new)
                .collect(Collectors.toList());

        PaginationDto paginationDto = new PaginationDto(
                answerPage.getNumber() + 1,
                answerPage.getTotalPages(),
                answerPage.getTotalElements()
        );

        return new AnswerHistoryResponseDto(answerDtos, paginationDto);
    }
}