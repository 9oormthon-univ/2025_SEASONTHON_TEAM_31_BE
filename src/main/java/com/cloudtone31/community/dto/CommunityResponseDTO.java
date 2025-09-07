package com.cloudtone31.community.dto;

import com.cloudtone31.community.domain.Community;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommunityResponseDTO {
    private Long id;
    private Long userId; // User 엔티티의 ID
    private String title;
    private String content;
//    private Integer likeCount;
    private LocalDateTime createdAt;

    public static CommunityResponseDTO from(Community community) {
        return new CommunityResponseDTO(
                community.getId(),
                community.getUser().getId(),
                community.getTitle(),
                community.getContent(),
//                community.getLikeCount(),
                community.getCreatedAt()
        );
    }

}
