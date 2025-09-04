package com.cloudtone31.userplants.domain;

import com.cloudtone31.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_plants")
public class UserPlants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user_plants.user_id -> users.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(name = "plant_type", nullable = false)
    private String plantType;

    @Column(name = "plant_name", nullable = false)
    private String plantName;

    @Column(name = "growth_percent", nullable = false)
    private int growthPercent;

    @Column(name = "plant_image")
    private String plantImage;

    @Column(name = "growth_stage")
    private String growthStage;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}