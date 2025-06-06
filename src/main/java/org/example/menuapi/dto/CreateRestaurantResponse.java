package org.example.menuapi.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRestaurantResponse {

    private UUID id;
    private String tinyId;
    private String restaurantName;
    private String addressLine1;
    private String city;
    private String state;
    private String pincode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String uiTemplate;
    private Set<String> cuisineTypes;
    private List<TimingResponse> timings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimingResponse {
        private String dayOfWeek;
        private LocalTime openTime;
        private LocalTime closeTime;
    }
}