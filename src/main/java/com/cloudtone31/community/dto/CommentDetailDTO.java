package com.cloudtone31.community.dto;

import com.cloudtone31.community.domain.Comments;

import java.time.LocalDateTime;

public record CommentDetailDTO(
        Long id,
        UserSummaryDTO user,
        String content,
        LocalDateTime createdAt
) {

    public static CommentDetailDTO from(Comments comment) {
        return new CommentDetailDTO(
                comment.getId(),
                UserSummaryDTO.from(comment.getUser()),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

}
