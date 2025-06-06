package org.example.menuapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "restaurant", indexes = {
        @Index(name = "idx_restaurant_location", columnList = "city, state, latitude, longitude")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

        @Id
        @GeneratedValue(generator = "UUID")
        @Column(columnDefinition = "UUID")
        private UUID id;

        @Column(name = "tiny_id", unique = true, nullable = false)
        private String tinyId;

        @Column(name = "restaurant_name", nullable = false)
        private String restaurantName;

        @Column(name = "address_line1")
        private String addressLine1;

        private String city;

        private String state;

        private String pincode;

        @Column(precision = 10, scale = 6)
        private BigDecimal latitude;

        @Column(precision = 10, scale = 6)
        private BigDecimal longitude;

        @CreationTimestamp
        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at", nullable = false)
        private LocalDateTime updatedAt;

        @Column(name = "ui_template")
        private String uiTemplate;

        @ManyToMany
        @JoinTable(
                name = "restaurant_cuisine_type_mapping",
                joinColumns = @JoinColumn(name = "restaurant_id"),
                inverseJoinColumns = @JoinColumn(name = "cuisine_type_id")
        )
        private Set<CuisineType> cuisineTypes;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
        private Set<RestaurantTiming> timings;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
        private Set<DishCategory> dishCategories;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
        private Set<Dish> dishes;
}