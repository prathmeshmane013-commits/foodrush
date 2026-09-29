package com.foodrush.order.dto;

import com.foodrush.order.entity.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(Long menuItemId, String name, BigDecimal price, int quantity, BigDecimal subtotal) {

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(item.getMenuItemId(), item.getNameSnapshot(),
                item.getPriceSnapshot(), item.getQuantity(), item.getSubtotal());
    }
}
