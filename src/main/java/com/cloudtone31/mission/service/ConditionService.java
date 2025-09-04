package com.cloudtone31.mission.service;

import com.cloudtone31.mission.domain.DailyCondition;
import com.cloudtone31.mission.domain.Users;
import com.cloudtone31.mission.dto.ConditionStatDto;
import com.cloudtone31.mission.dto.ConditionStatsResponseDto;
import com.cloudtone31.mission.repository.DailyConditionRepository;
import com.cloudtone31.mission.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final DailyConditionRepository dailyConditionRepository;
    private final UserRepository userRepository;

    @Transactional
    public DailyCondition createDailyCondition(Long userId, String condition) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. id=" + userId));

        DailyCondition newDailyCondition = new DailyCondition(user, condition);

        return dailyConditionRepository.save(newDailyCondition);
    }

    @Transactional(readOnly = true)
    public ConditionStatsResponseDto getConditionStats(Long userId, String period) {
        LocalDate today = LocalDate.now();
        LocalDateTime startDateTime;
        LocalDateTime endDateTime = LocalDateTime.of(today, LocalTime.MAX); // 오늘 날짜의 끝 시간

        switch (period) {
            case "month":
                startDateTime = LocalDateTime.of(today.with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN);
                break;
            case "year":
                startDateTime = LocalDateTime.of(today.with(TemporalAdjusters.firstDayOfYear()), LocalTime.MIN);
                break;
            case "week":
            default:
                startDateTime = LocalDateTime.of(today.with(java.time.DayOfWeek.MONDAY), LocalTime.MIN);
                break;
        }

        List<DailyCondition> conditions = dailyConditionRepository.findByUserIdAndSelectedAtBetween(userId, startDateTime, endDateTime);

        List<ConditionStatDto> statistics = conditions.stream()
                .map(ConditionStatDto::new)
                .collect(Collectors.toList());

        Map<String, Long> summary = conditions.stream()
                .collect(Collectors.groupingBy(DailyCondition::getCondition, Collectors.counting()));

        return new ConditionStatsResponseDto(period, statistics, summary);
    }
}