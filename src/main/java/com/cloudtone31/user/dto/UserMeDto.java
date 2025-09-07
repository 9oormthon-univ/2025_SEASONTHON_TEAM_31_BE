package com.cloudtone31.user.dto;

import com.cloudtone31.user.domain.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZoneId;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserMeDto {

    private final Long id;

    @JsonProperty("kakao_id")
    private final String kakaoId;

    private final String name;      // 없으면 nickname과 동일값
    private final String nickname;

    @JsonProperty("created_at")
    private final String createdAt; // ISO-8601(UTC)

    @JsonProperty("updated_at")
    private final String updatedAt;

    public UserMeDto(Long id, String kakaoId, String name, String nickname, String createdAt, String updatedAt) {
        this.id = id;
        this.kakaoId = kakaoId;
        this.name = name;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserMeDto of(User u) {
        ZoneId sys = ZoneId.systemDefault();
        String created = u.getCreatedAt() == null ? null : u.getCreatedAt().atZone(sys).toInstant().toString();
        String updated = u.getUpdatedAt() == null ? null : u.getUpdatedAt().atZone(sys).toInstant().toString();

        return new UserMeDto(
                u.getId(),
                u.getKakaoId() == null ? null : String.valueOf(u.getKakaoId()),
                u.getNickname(),     // name은 우선 nickname과 동일하게
                u.getNickname(),
                created,
                updated
        );
    }

    public Long getId() { return id; }
    public String getKakaoId() { return kakaoId; }
    public String getName() { return name; }
    public String getNickname() { return nickname; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}