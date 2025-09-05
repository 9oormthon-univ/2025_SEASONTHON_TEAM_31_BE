package com.cloudtone31.mission.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType; // 추가
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "missions")
@Getter
@NoArgsConstructor
public class Missions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 예약어 충돌 회피
    @Column(name = "condition", nullable = false, length = 255)
    private String condition;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    // MySQL 8.x: json 타입 권장 (H2 테스트 시엔 TEXT로 교체)
    @Type(JsonType.class)
    @Column(name = "options", columnDefinition = "json")
    private List<String> options;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}