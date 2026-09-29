package com.foodrush.restaurant.dto;

import jakarta.validation.constraints.NotBlank;

public record RestaurantRequest(
        @NotBlank(message = "Name is required") String name,
        String description,
        @NotBlank(message = "Address is required") String address,
        String cuisine
) {}
