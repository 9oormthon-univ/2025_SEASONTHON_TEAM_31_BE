package com.cloudtone31.community.repository;

import com.cloudtone31.community.domain.Community;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {

}
