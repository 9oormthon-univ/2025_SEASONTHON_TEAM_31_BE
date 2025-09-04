package com.cloudtone31.mission.repository;
import com.cloudtone31.mission.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {

}
