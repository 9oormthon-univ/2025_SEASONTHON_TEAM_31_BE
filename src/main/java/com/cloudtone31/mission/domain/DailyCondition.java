package com.cloudtone31.mission.domain;

import com.cloudtone31.user.domain.User;
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
    private User user;

    @Column(name = "`condition`", nullable = false) // 'condition'은 SQL 예약어일 수 있으므로 ``로 감싸줍니다.
    private String condition;

    private LocalDateTime selectedAt;

    public DailyCondition(User user, String condition) {
        this.user = user;
        this.condition = condition;
        this.selectedAt = LocalDateTime.now();
    }
}