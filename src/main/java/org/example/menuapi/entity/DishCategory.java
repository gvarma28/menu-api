package org.example.menuapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "dish_category",
        uniqueConstraints = @UniqueConstraint(columnNames = {"restaurant_id", "category_name", "parent_id"}),
        indexes = {
                @Index(name = "idx_category_restaurant", columnList = "restaurant_id")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishCategory {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private DishCategory parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DishCategory> subCategories;

    @OneToMany(mappedBy = "dishCategory", cascade = CascadeType.ALL)
    private Set<Dish> dishes;
}
