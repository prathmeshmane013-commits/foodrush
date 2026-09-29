package com.foodrush.order.service;

import com.foodrush.order.client.MenuItemResponse;
import com.foodrush.order.client.RestaurantClient;
import com.foodrush.order.dto.*;
import com.foodrush.order.entity.Order;
import com.foodrush.order.entity.OrderItem;
import com.foodrush.order.entity.OrderStatus;
import com.foodrush.order.exception.InvalidOrderException;
import com.foodrush.order.exception.ResourceNotFoundException;
import com.foodrush.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantClient restaurantClient;

    public OrderService(OrderRepository orderRepository, RestaurantClient restaurantClient) {
        this.orderRepository = orderRepository;
        this.restaurantClient = restaurantClient;
    }

    public OrderResponse placeOrder(String customerEmail, PlaceOrderRequest request) {
        Order order = new Order();
        order.setCustomerEmail(customerEmail);
        order.setRestaurantId(request.restaurantId());
        order.setDeliveryAddress(request.deliveryAddress().trim());
        order.setStatus(OrderStatus.PLACED);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            // This call to restaurant-service is the inter-service part: we don't
            // trust a price the client might send, we look it up fresh every time.
            MenuItemResponse menuItem = restaurantClient.getMenuItem(itemRequest.menuItemId());

            if (!menuItem.restaurantId().equals(request.restaurantId())) {
                throw new InvalidOrderException(
                        "Menu item " + menuItem.id() + " does not belong to restaurant " + request.restaurantId());
            }
            if (!menuItem.available()) {
                throw new InvalidOrderException("Menu item is not available: " + menuItem.name());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setMenuItemId(menuItem.id());
            item.setNameSnapshot(menuItem.name());
            item.setPriceSnapshot(menuItem.price());
            item.setQuantity(itemRequest.quantity());
            order.getItems().add(item);

            total = total.add(item.getSubtotal());
        }

        order.setTotalAmount(total);
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String customerEmail) {
        return orderRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail).stream()
                .map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id, String requesterEmail, boolean isAdmin) {
        Order order = findOrThrow(id);
        if (!isAdmin && !order.getCustomerEmail().equalsIgnoreCase(requesterEmail)) {
            throw new ResourceNotFoundException("Order not found: " + id);
        }
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(OrderResponse::from).toList();
    }

    public OrderResponse updateStatus(Long id, OrderStatus newStatus) {
        Order order = findOrThrow(id);
        order.setStatus(newStatus);
        return OrderResponse.from(orderRepository.save(order));
    }

    private Order findOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }
}
