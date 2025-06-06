package org.example.menuapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "customization_group", indexes = {
        @Index(name = "idx_customization_group_dish", columnList = "dish_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomizationGroup {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    @Column(name = "group_name")
    private String groupName;

    @Enumerated(EnumType.STRING)
    private CustomizationType type;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CustomizationOption> options;

    public enum CustomizationType {
        direct, extra
    }
}