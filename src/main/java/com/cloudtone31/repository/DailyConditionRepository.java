package com.cloudtone31.repository;

import com.cloudtone31.domain.DailyCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DailyConditionRepository extends JpaRepository<DailyCondition, Long> {

    List<DailyCondition> findByUserIdAndSelectedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}