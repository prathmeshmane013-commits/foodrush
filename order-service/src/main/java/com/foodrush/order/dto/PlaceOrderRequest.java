package com.foodrush.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlaceOrderRequest(
        @NotNull(message = "Restaurant id is required") Long restaurantId,
        @NotBlank(message = "Delivery address is required") String deliveryAddress,
        @NotEmpty(message = "Order must have at least one item")
        List<@Valid OrderItemRequest> items
) {}
