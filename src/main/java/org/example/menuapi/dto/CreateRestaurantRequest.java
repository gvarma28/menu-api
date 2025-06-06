package org.example.menuapi.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRestaurantRequest {
    @NotBlank(message = "Restaurant name is required")
    private String restaurantName;

    @Size(max = 300, message = "Address line 1 cannot exceed 300 characters")
    private String addressLine1;

    @Size(max = 50, message = "City name cannot exceed 50 characters")
    private String city;

    @Size(max = 50, message = "State name cannot exceed 50 characters")
    private String state;

    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal longitude;

    private String uiTemplate;

    @NotEmpty(message = "At least one cuisine type is required")
    private Set<String> cuisineTypes;

    private List<RestaurantTimingRequest> timings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RestaurantTimingRequest {
        @NotNull(message = "Day of week is required")
        private String dayOfWeek; // Mon, Tue, Wed, Thu, Fri, Sat, Sun

        private LocalTime openTime;
        private LocalTime closeTime;
    }
}
