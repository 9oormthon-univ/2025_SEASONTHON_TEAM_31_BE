package com.cloudtone31.community.service;

import com.cloudtone31.community.domain.Community;
import com.cloudtone31.community.domain.CommunityLike;
import com.cloudtone31.community.repository.CommunityLikeRepository;
import com.cloudtone31.community.repository.CommunityRepository;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.cloudtone31.community.domain.CommunityLike.*;

@Service
@RequiredArgsConstructor
public class CommunityLikeService {

    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityRepository communityRepository;
    private final UserLoginRepository userLoginRepository;

    @Transactional
    public void addLike(Long postId, Long userId) {
        User user = userLoginRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Community community = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        // 이미 좋아요를 눌렀는지 확인하고, 눌렀다면 예외 발생
        if (communityLikeRepository.findByUserAndCommunity(user, community).isPresent()) {
            throw new IllegalArgumentException("이미 좋아요를 누른 게시물입니다.");
        }

        // 좋아요 추가
        CommunityLike newLike = builder()
                .user(user)
                .community(community)
                .build();
        communityLikeRepository.save(newLike);
        community.incrementLikeCount();
    }


}
