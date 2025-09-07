package com.cloudtone31.community.dto;

import java.util.List;

public record CommunityListResponseDTO(
        List<CommunitySummaryDTO> posts,
        PaginationDTO pagintaion
) {
}
