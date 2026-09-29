package com.foodrush.restaurant.repository;

import com.foodrush.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByCuisineIgnoreCase(String cuisine);
}
