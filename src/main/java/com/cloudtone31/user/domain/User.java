package com.cloudtone31.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_kakao_id", columnList = "kakaoId", unique = true)
})
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long kakaoId;

    private String email;
    private String nickname;
    private String profileImage;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    // === 엔티티 메서드 ===
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateLastLoginAt(LocalDateTime now) {
        this.lastLoginAt = now;
    }
}