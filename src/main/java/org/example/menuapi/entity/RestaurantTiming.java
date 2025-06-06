package org.example.menuapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "restaurant_timing", indexes = {
        @Index(name = "idx_timing_restaurant", columnList = "restaurant_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantTiming {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    public enum DayOfWeek {
        Mon, Tue, Wed, Thu, Fri, Sat, Sun
    }
}