package com.cloudtone31.community.repository;

import com.cloudtone31.community.domain.Community;
import com.cloudtone31.community.domain.CommunityLike;
import com.cloudtone31.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

    Optional<CommunityLike> findByUserAndCommunity(User user, Community community);

}
