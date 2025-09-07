package com.cloudtone31.community.dto;

import com.cloudtone31.community.domain.Community;

import java.time.LocalDateTime;
import java.util.List;

public record CommunityDetailDTO(
        Long id,
        UserSummaryDTO user,
        String title,
        String content,
        int likeCount,
        LocalDateTime createdAt,
        List<CommentDetailDTO> comments
) {
    public static CommunityDetailDTO from(Community community, List<CommentDetailDTO> comments) {
        return new CommunityDetailDTO(
                community.getId(),
                UserSummaryDTO.from(community.getUser()),
                community.getTitle(),
                community.getContent(),
                community.getLikeCount(),
                community.getCreatedAt(),
                comments
        );
    }
}
