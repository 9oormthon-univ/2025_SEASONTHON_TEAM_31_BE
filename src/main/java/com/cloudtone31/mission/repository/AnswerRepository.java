package com.cloudtone31.mission.repository;

import com.cloudtone31.mission.domain.Answers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answers, Long> {

    @Query("SELECT a.mission.id FROM Answers a WHERE a.user.id = :userId")
    List<Long> findAnsweredMissionIdsByUserId(@Param("userId") Long userId);
}