package com.foodrush.restaurant.dto;

import com.foodrush.restaurant.entity.Restaurant;

public record RestaurantResponse(Long id, String name, String description, String address, String cuisine) {

    public static RestaurantResponse from(Restaurant r) {
        return new RestaurantResponse(r.getId(), r.getName(), r.getDescription(), r.getAddress(), r.getCuisine());
    }
}
