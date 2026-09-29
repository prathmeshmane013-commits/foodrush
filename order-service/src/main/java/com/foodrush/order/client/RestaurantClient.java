package com.foodrush.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// "RESTAURANT-SERVICE" must match spring.application.name in restaurant-service.
// Eureka + the load balancer resolve it to an actual host:port at call time.
@FeignClient(name = "RESTAURANT-SERVICE")
public interface RestaurantClient {

    @GetMapping("/api/restaurants/menu-items/{itemId}")
    MenuItemResponse getMenuItem(@PathVariable("itemId") Long itemId);
}
