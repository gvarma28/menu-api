package org.example.menuapi.controller;

import org.example.menuapi.dto.CreateRestaurantRequest;
import org.example.menuapi.dto.CreateRestaurantResponse;
import org.example.menuapi.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping("/onboard")
    public ResponseEntity<CreateRestaurantResponse> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest request) {

        CreateRestaurantResponse response = restaurantService.createRestaurant(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}