package com.cloudtone31.userplants.repository;

import com.cloudtone31.userplants.domain.PlantType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlantTypeRepository extends JpaRepository<PlantType, Long> {

    List<PlantType> findAllByOrderByUnlockOrderAsc();
}