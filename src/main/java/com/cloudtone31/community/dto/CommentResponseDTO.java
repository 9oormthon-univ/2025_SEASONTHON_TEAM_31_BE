package com.cloudtone31.community.dto;

import com.cloudtone31.community.domain.Comments;

import java.time.LocalDateTime;

public record CommentResponseDTO(
        Long id,
        Long userId,
        Long communityId,
        String content,
        LocalDateTime createdAt
) {

    public static CommentResponseDTO from(Comments comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getUser().getId(),
                comment.getCommunity().getId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

}
