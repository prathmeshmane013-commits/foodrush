package com.foodrush.order.client;

import java.math.BigDecimal;

/** Mirrors restaurant-service's MenuItemResponse. Only order-service's own copy, kept in sync by hand. */
public record MenuItemResponse(Long id, Long restaurantId, String name, String description,
                               BigDecimal price, boolean available) {}
