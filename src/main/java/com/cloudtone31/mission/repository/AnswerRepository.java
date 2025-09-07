package com.cloudtone31.mission.repository;

import com.cloudtone31.mission.domain.Answers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answers, Long> {

    @Query("SELECT a.mission.id FROM Answers a WHERE a.user.id = :userId")
    List<Long> findAnsweredMissionIdsByUserId(@Param("userId") Long userId);

    // 날짜 필터링을 포함한 답변 조회 쿼리 추가
    @Query("SELECT a FROM Answers a WHERE a.user.id = :userId " +
            "AND (:startDate IS NULL OR a.answeredAt >= :startDate) " +
            "AND (:endDate IS NULL OR a.answeredAt <= :endDate) " +
            "ORDER BY a.answeredAt DESC")
    Page<Answers> findByUserIdWithFilters(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}