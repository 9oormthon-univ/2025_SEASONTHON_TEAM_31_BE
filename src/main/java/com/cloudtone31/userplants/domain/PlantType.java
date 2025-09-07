package com.cloudtone31.userplants.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "plant_types")
public class PlantType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_code", nullable = false, unique = true)
    private String typeCode;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "unlock_order", nullable = false)
    private int unlockOrder;
}