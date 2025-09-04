package com.cloudtone31.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType; // 추가
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Missions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String condition;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false)
    private Integer questionOrder;

    @Type(JsonType.class)
    @Column(columnDefinition = "VARCHAR(1024)")
    private List<String> options;

    private LocalDateTime createdAt;
}