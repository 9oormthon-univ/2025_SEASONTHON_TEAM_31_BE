package com.cloudtone31.mission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaginationDto {
    private int currentPage;
    private int totalPages;
    private long totalCount;
}