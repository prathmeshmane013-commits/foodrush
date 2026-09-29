package com.foodrush.order.dto;

import com.foodrush.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(@NotNull(message = "Status is required") OrderStatus status) {}
