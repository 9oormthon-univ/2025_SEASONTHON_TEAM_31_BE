package com.cloudtone31.community.dto;

import org.springframework.data.domain.Page;

public record PaginationDTO(
        int currentPage,
        int totalPages,
        long totalCount,
        boolean hasNext
) {

    public static PaginationDTO from(Page<?> page){
        return new PaginationDTO(
                page.getNumber()+1,
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext()
        );
    }

}
