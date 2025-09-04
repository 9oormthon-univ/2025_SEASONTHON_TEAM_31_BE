package com.cloudtone31.repository;

import com.cloudtone31.domain.Missions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// JpaRepository<관리할 Entity, 해당 Entity의 PK 타입>
public interface MissionRepository extends JpaRepository<Missions, Long> {


    @Query("SELECT m FROM Missions m WHERE m.condition = :condition AND m.id NOT IN :answeredMissionIds")
    List<Missions> findMissionsByConditionAndIdNotIn(@Param("condition") String condition, @Param("answeredMissionIds") List<Long> answeredMissionIds);


    List<Missions> findByCondition(String condition);
}