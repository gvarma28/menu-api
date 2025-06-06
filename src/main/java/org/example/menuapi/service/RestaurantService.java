package org.example.menuapi.service;

import org.example.menuapi.dto.CreateRestaurantRequest;
import org.example.menuapi.dto.CreateRestaurantResponse;
import org.example.menuapi.entity.*;
import org.example.menuapi.repository.RestaurantRepository;
import org.example.menuapi.repository.CuisineTypeRepository;
import org.example.menuapi.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final CuisineTypeRepository cuisineTypeRepository;
    private final TinyIdGenerator tinyIdGenerator;

    /**
     * Create a new restaurant
     */
    public CreateRestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        log.info("Creating new restaurant: {}", request.getRestaurantName());

        // Validate business rules
        validateRestaurantRequest(request);

        // Generate unique tiny ID
        String tinyId = generateUniqueTinyId();

        // Get or create cuisine types
        Set<CuisineType> cuisineTypes = getOrCreateCuisineTypes(request.getCuisineTypes());

        // Build restaurant entity
        Restaurant restaurant = Restaurant.builder()
                .tinyId(tinyId)
                .restaurantName(request.getRestaurantName())
                .addressLine1(request.getAddressLine1())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .uiTemplate(request.getUiTemplate())
                .cuisineTypes(cuisineTypes)
                .build();

        // Save restaurant first
        restaurant = restaurantRepository.save(restaurant);

        // Add timings if provided
        if (request.getTimings() != null && !request.getTimings().isEmpty()) {
            addRestaurantTimings(restaurant, request.getTimings());
        }

        log.info("Restaurant created successfully with ID: {} and tiny ID: {}",
                restaurant.getId(), restaurant.getTinyId());

        return mapToResponse(restaurant);
    }

    /**
     * Validate restaurant creation request
     */
    private void validateRestaurantRequest(CreateRestaurantRequest request) {
        // Check for duplicate restaurant name in same city
        if (request.getCity() != null) {
            List<Restaurant> existingRestaurants = restaurantRepository
                    .findByCityIgnoreCaseAndStateIgnoreCase(request.getCity(), request.getState());

            boolean duplicateExists = existingRestaurants.stream()
                    .anyMatch(r -> r.getRestaurantName().equalsIgnoreCase(request.getRestaurantName()));

            if (duplicateExists) {
                throw new BusinessException("Restaurant with this name already exists in " + request.getCity());
            }
        }

        // Validate coordinate consistency
        if ((request.getLatitude() != null && request.getLongitude() == null) ||
                (request.getLatitude() == null && request.getLongitude() != null)) {
            throw new BusinessException("Both latitude and longitude must be provided together");
        }

        // Validate timings
        if (request.getTimings() != null) {
            validateTimings(request.getTimings());
        }
    }

    /**
     * Validate restaurant timings
     */
    private void validateTimings(List<CreateRestaurantRequest.RestaurantTimingRequest> timings) {
        Set<String> daysProvided = new HashSet<>();

        for (CreateRestaurantRequest.RestaurantTimingRequest timing : timings) {
            // Check for duplicate days
            if (daysProvided.contains(timing.getDayOfWeek())) {
                throw new BusinessException("Duplicate timing entry for day: " + timing.getDayOfWeek());
            }
            daysProvided.add(timing.getDayOfWeek());

            // Validate day of week
            try {
                RestaurantTiming.DayOfWeek.valueOf(timing.getDayOfWeek());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Invalid day of week: " + timing.getDayOfWeek());
            }

            // Validate time consistency
            if (timing.getOpenTime() != null && timing.getCloseTime() != null) {
                if (timing.getOpenTime().equals(timing.getCloseTime())) {
                    throw new BusinessException("Open time and close time cannot be the same for " + timing.getDayOfWeek());
                }
            }

            // Check for partial timing (either both or neither should be provided)
            if ((timing.getOpenTime() != null && timing.getCloseTime() == null) ||
                    (timing.getOpenTime() == null && timing.getCloseTime() != null)) {
                throw new BusinessException("Both open time and close time must be provided for " + timing.getDayOfWeek());
            }
        }
    }

    /**
     * Generate unique tiny ID
     */
    private String generateUniqueTinyId() {
        String tinyId;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            tinyId = tinyIdGenerator.generate();
            attempts++;

            if (attempts > maxAttempts) {
                throw new BusinessException("Unable to generate unique tiny ID after " + maxAttempts + " attempts");
            }
        } while (restaurantRepository.existsByTinyId(tinyId));

        return tinyId;
    }

    /**
     * Get existing cuisine types or create new ones
     */
    private Set<CuisineType> getOrCreateCuisineTypes(Set<String> cuisineTypeNames) {
        Set<CuisineType> cuisineTypes = new HashSet<>();

        for (String cuisineName : cuisineTypeNames) {
            CuisineType cuisineType = cuisineTypeRepository.findByNameIgnoreCase(cuisineName)
                    .orElseGet(() -> {
                        log.info("Creating new cuisine type: {}", cuisineName);
                        return cuisineTypeRepository.save(
                                CuisineType.builder().name(cuisineName).build()
                        );
                    });
            cuisineTypes.add(cuisineType);
        }

        return cuisineTypes;
    }

    /**
     * Add restaurant timings
     */
    private void addRestaurantTimings(Restaurant restaurant,
                                      List<CreateRestaurantRequest.RestaurantTimingRequest> timingRequests) {
        Set<RestaurantTiming> timings = timingRequests.stream()
                .map(timingRequest -> RestaurantTiming.builder()
                        .restaurant(restaurant)
                        .dayOfWeek(RestaurantTiming.DayOfWeek.valueOf(timingRequest.getDayOfWeek()))
                        .openTime(timingRequest.getOpenTime())
                        .closeTime(timingRequest.getCloseTime())
                        .build())
                .collect(Collectors.toSet());

        restaurant.setTimings(timings);
        restaurantRepository.save(restaurant);
    }

    /**
     * Map restaurant entity to response DTO
     */
    private CreateRestaurantResponse mapToResponse(Restaurant restaurant) {
        Set<String> cuisineTypeNames = restaurant.getCuisineTypes() != null ?
                restaurant.getCuisineTypes().stream()
                        .map(CuisineType::getName)
                        .collect(Collectors.toSet()) : new HashSet<>();

        List<CreateRestaurantResponse.TimingResponse> timingResponses =
                restaurant.getTimings() != null ?
                        restaurant.getTimings().stream()
                                .map(timing -> CreateRestaurantResponse.TimingResponse.builder()
                                        .dayOfWeek(timing.getDayOfWeek().name())
                                        .openTime(timing.getOpenTime())
                                        .closeTime(timing.getCloseTime())
                                        .build())
                                .collect(Collectors.toList()) : new ArrayList<>();

        return CreateRestaurantResponse.builder()
                .id(restaurant.getId())
                .tinyId(restaurant.getTinyId())
                .restaurantName(restaurant.getRestaurantName())
                .addressLine1(restaurant.getAddressLine1())
                .city(restaurant.getCity())
                .state(restaurant.getState())
                .pincode(restaurant.getPincode())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())
                .uiTemplate(restaurant.getUiTemplate())
                .cuisineTypes(cuisineTypeNames)
                .timings(timingResponses)
                .build();
    }
}