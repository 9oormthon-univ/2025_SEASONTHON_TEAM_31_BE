package com.cloudtone31.userplants.repository;

import com.cloudtone31.userplants.domain.UserPlants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPlantsRepository extends JpaRepository<com.cloudtone31.userplants.domain.UserPlants, Long> {
    Optional<com.cloudtone31.userplants.domain.UserPlants> findByUserIdAndIsActive(Long userId, boolean isActive);

    List<UserPlants> findAllByUserId(Long userId);
}