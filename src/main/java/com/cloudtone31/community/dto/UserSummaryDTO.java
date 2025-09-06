package com.cloudtone31.community.dto;


import com.cloudtone31.user.domain.User;

public record UserSummaryDTO (
        Long id,
        String nickname
){
    public static UserSummaryDTO from(User user) {
        if (user == null) {
            return new UserSummaryDTO(null, "탈퇴한 사용자");
        }
        return new UserSummaryDTO(user.getId(), user.getNickname());
    }
}
