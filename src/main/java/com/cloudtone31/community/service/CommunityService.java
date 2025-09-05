package com.cloudtone31.community.service;

import com.cloudtone31.community.domain.Community;
import com.cloudtone31.community.dto.CommunityRequestDTO;
import com.cloudtone31.community.repository.CommunityRepository;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserLoginRepository userLoginRepository;

    @Transactional
    public Community create(CommunityRequestDTO requestDTO, Long userId) {
        User user = userLoginRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        Community community = Community.builder()
                .user(user)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .build();

        return communityRepository.save(community);

    }


}
