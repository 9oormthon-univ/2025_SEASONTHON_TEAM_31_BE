package com.cloudtone31.community.service;

import com.cloudtone31.community.domain.Community;
import com.cloudtone31.community.dto.*;
import com.cloudtone31.community.repository.CommentRepository;
import com.cloudtone31.community.repository.CommunityRepository;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserLoginRepository userLoginRepository;
    private final CommentRepository commentRepository;

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

    public CommunityListResponseDTO getCommunityList(int page, int limit, String sort){

        Sort sorting = Sort.by(Sort.Direction.DESC, "createdAt");
        if("popular".equalsIgnoreCase(sort)){
            sorting = Sort.by(Sort.Direction.DESC, "likeCount");
        }

        Pageable pageable = PageRequest.of(page-1, limit, sorting);
        Page<Community> communityPage=  communityRepository.findAll(pageable);

        List<CommunitySummaryDTO> postSummaries = communityPage.getContent().stream()
                .map(CommunitySummaryDTO::from)
                .toList();

        PaginationDTO paginationDTO = PaginationDTO.from(communityPage);

        return new CommunityListResponseDTO(postSummaries, paginationDTO);
    }

    public CommunityDetailDTO getCommunityDetail(Long postId) {
        Community community = communityRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        var comments = commentRepository.findByCommunityOrderByCreatedAtAsc(community);

        List<CommentDetailDTO> commentDTOs = comments.stream()
                .map(CommentDetailDTO::from)
                .toList();

        return CommunityDetailDTO.from(community, commentDTOs);

    }


}
