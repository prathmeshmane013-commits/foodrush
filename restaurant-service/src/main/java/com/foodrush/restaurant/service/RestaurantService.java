package com.foodrush.restaurant.service;

import com.foodrush.restaurant.dto.RestaurantRequest;
import com.foodrush.restaurant.dto.RestaurantResponse;
import com.foodrush.restaurant.entity.Restaurant;
import com.foodrush.restaurant.exception.ResourceNotFoundException;
import com.foodrush.restaurant.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAll(String cuisine) {
        List<Restaurant> restaurants = (cuisine == null || cuisine.isBlank())
                ? restaurantRepository.findAll()
                : restaurantRepository.findByCuisineIgnoreCase(cuisine.trim());
        return restaurants.stream().map(RestaurantResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getById(Long id) {
        return RestaurantResponse.from(findOrThrow(id));
    }

    public RestaurantResponse create(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        apply(restaurant, request);
        return RestaurantResponse.from(restaurantRepository.save(restaurant));
    }

    public RestaurantResponse update(Long id, RestaurantRequest request) {
        Restaurant restaurant = findOrThrow(id);
        apply(restaurant, request);
        return RestaurantResponse.from(restaurantRepository.save(restaurant));
    }

    public void delete(Long id) {
        restaurantRepository.delete(findOrThrow(id));
    }

    private Restaurant findOrThrow(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + id));
    }

    private void apply(Restaurant restaurant, RestaurantRequest request) {
        restaurant.setName(request.name().trim());
        restaurant.setDescription(request.description());
        restaurant.setAddress(request.address().trim());
        restaurant.setCuisine(request.cuisine());
    }
}
