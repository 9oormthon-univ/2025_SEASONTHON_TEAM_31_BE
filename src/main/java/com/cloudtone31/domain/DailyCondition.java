package com.cloudtone31.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "daily_conditions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "`condition`", nullable = false) // 'condition'은 SQL 예약어일 수 있으므로 ``로 감싸줍니다.
    private String condition;

    private LocalDateTime selectedAt;

    public DailyCondition(Users user, String condition) {
        this.user = user;
        this.condition = condition;
        this.selectedAt = LocalDateTime.now();
    }
}