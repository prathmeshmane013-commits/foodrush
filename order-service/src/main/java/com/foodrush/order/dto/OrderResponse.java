package com.foodrush.order.dto;

import com.foodrush.order.entity.Order;
import com.foodrush.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, String customerEmail, Long restaurantId, String deliveryAddress,
                            OrderStatus status, BigDecimal totalAmount, LocalDateTime createdAt,
                            List<OrderItemResponse> items) {

    public static OrderResponse from(Order order) {
        List<OrderItemResponse> items = order.getItems().stream().map(OrderItemResponse::from).toList();
        return new OrderResponse(order.getId(), order.getCustomerEmail(), order.getRestaurantId(),
                order.getDeliveryAddress(), order.getStatus(), order.getTotalAmount(),
                order.getCreatedAt(), items);
    }
}
