package com.cloudtone31.community.service;

import com.cloudtone31.community.domain.Comments;
import com.cloudtone31.community.domain.Community;
import com.cloudtone31.community.dto.CommentRequestDTO;
import com.cloudtone31.community.repository.CommentRepository;
import com.cloudtone31.community.repository.CommunityRepository;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;
    private final UserLoginRepository userLoginRepository;

    @Transactional
    public Comments  createComment(Long communityId, Long userId, CommentRequestDTO commentRequestDTO) {

        User user = userLoginRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(()-> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + communityId));

        Comments newComment = Comments.builder()
                .user(user)
                .community(community)
                .content(commentRequestDTO.content())
                .build();

        return commentRepository.save(newComment);

    }

}
