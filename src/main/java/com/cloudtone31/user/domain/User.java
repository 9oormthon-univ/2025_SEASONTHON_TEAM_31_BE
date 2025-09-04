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
        @Index(name = "idx_users_kakao_id", columnList = "kakao_id", unique = true)
})
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // DB: varchar → String, 컬럼명 매핑
    @Column(name = "kakao_id", nullable = false, unique = true)
    private String kakaoId;

    // DB: NOT NULL → 엔티티에도 필수
    @Column(nullable = false)
    private String name;

    private String email;
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    // 편의 메서드
    public void updateNickname(String nickname) { this.nickname = nickname; }
    public void updateLastLoginAt(LocalDateTime now) { this.lastLoginAt = now; }
}