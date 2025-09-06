package com.cloudtone31.community.dto;

import com.cloudtone31.community.domain.Community;

import java.time.LocalDateTime;

public record CommunitySummaryDTO(
        Long id,
        UserSummaryDTO user,
        String title,
        String content,
        int likeCount,
//        int commentCount,
        LocalDateTime createdAt
) {
    public static CommunitySummaryDTO from(Community community) {
        String summaryContent = community.getContent();
        if (summaryContent != null && summaryContent.length() > 100) {
            summaryContent = summaryContent.substring(0, 100) + "...";
        }

        return new CommunitySummaryDTO(
                community.getId(),
                UserSummaryDTO.from(community.getUser()),
                community.getTitle(),
                summaryContent,
                community.getLikeCount(),
                community.getCreatedAt()
        );
    }
}

