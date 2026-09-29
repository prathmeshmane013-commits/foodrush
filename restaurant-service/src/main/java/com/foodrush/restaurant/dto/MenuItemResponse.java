package com.foodrush.restaurant.dto;

import com.foodrush.restaurant.entity.MenuItem;

import java.math.BigDecimal;

public record MenuItemResponse(Long id, Long restaurantId, String name, String description,
                               BigDecimal price, boolean available) {

    public static MenuItemResponse from(MenuItem m) {
        return new MenuItemResponse(m.getId(), m.getRestaurant().getId(), m.getName(),
                m.getDescription(), m.getPrice(), m.isAvailable());
    }
}
