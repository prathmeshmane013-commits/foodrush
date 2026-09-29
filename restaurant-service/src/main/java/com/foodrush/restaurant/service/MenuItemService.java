package com.foodrush.restaurant.service;

import com.foodrush.restaurant.dto.MenuItemRequest;
import com.foodrush.restaurant.dto.MenuItemResponse;
import com.foodrush.restaurant.entity.MenuItem;
import com.foodrush.restaurant.entity.Restaurant;
import com.foodrush.restaurant.exception.ResourceNotFoundException;
import com.foodrush.restaurant.repository.MenuItemRepository;
import com.foodrush.restaurant.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuItemService(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenu(Long restaurantId) {
        checkRestaurantExists(restaurantId);
        return menuItemRepository.findByRestaurantId(restaurantId).stream()
                .map(MenuItemResponse::from).toList();
    }

    /** Used later by the order service to look up the price of one item. */
    @Transactional(readOnly = true)
    public MenuItemResponse getItem(Long itemId) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + itemId));
        return MenuItemResponse.from(item);
    }

    public MenuItemResponse create(Long restaurantId, MenuItemRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + restaurantId));

        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        apply(item, request);
        return MenuItemResponse.from(menuItemRepository.save(item));
    }

    public MenuItemResponse update(Long restaurantId, Long itemId, MenuItemRequest request) {
        MenuItem item = findInRestaurant(restaurantId, itemId);
        apply(item, request);
        return MenuItemResponse.from(menuItemRepository.save(item));
    }

    public void delete(Long restaurantId, Long itemId) {
        menuItemRepository.delete(findInRestaurant(restaurantId, itemId));
    }

    private MenuItem findInRestaurant(Long restaurantId, Long itemId) {
        return menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu item " + itemId + " not found in restaurant " + restaurantId));
    }

    private void checkRestaurantExists(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found: " + restaurantId);
        }
    }

    private void apply(MenuItem item, MenuItemRequest request) {
        item.setName(request.name().trim());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setAvailable(request.available() == null || request.available());
    }
}
