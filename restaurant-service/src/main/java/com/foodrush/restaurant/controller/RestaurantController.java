package com.foodrush.restaurant.controller;

import com.foodrush.restaurant.dto.MenuItemRequest;
import com.foodrush.restaurant.dto.MenuItemResponse;
import com.foodrush.restaurant.dto.RestaurantRequest;
import com.foodrush.restaurant.dto.RestaurantResponse;
import com.foodrush.restaurant.service.MenuItemService;
import com.foodrush.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;

    public RestaurantController(RestaurantService restaurantService, MenuItemService menuItemService) {
        this.restaurantService = restaurantService;
        this.menuItemService = menuItemService;
    }

    // ---------- Restaurants ----------

    @GetMapping
    public List<RestaurantResponse> getAll(@RequestParam(required = false) String cuisine) {
        return restaurantService.getAll(cuisine);
    }

    @GetMapping("/{id}")
    public RestaurantResponse getOne(@PathVariable Long id) {
        return restaurantService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse create(@Valid @RequestBody RestaurantRequest request) {
        return restaurantService.create(request);
    }

    @PutMapping("/{id}")
    public RestaurantResponse update(@PathVariable Long id, @Valid @RequestBody RestaurantRequest request) {
        return restaurantService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        restaurantService.delete(id);
    }

    // ---------- Menu items ----------

    @GetMapping("/{id}/menu")
    public List<MenuItemResponse> getMenu(@PathVariable Long id) {
        return menuItemService.getMenu(id);
    }

    @PostMapping("/{id}/menu")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse addMenuItem(@PathVariable Long id, @Valid @RequestBody MenuItemRequest request) {
        return menuItemService.create(id, request);
    }

    @PutMapping("/{id}/menu/{itemId}")
    public MenuItemResponse updateMenuItem(@PathVariable Long id, @PathVariable Long itemId,
                                           @Valid @RequestBody MenuItemRequest request) {
        return menuItemService.update(id, itemId, request);
    }

    @DeleteMapping("/{id}/menu/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable Long id, @PathVariable Long itemId) {
        menuItemService.delete(id, itemId);
    }

    // Look up one menu item by its id (the order service will call this later)
    @GetMapping("/menu-items/{itemId}")
    public MenuItemResponse getMenuItem(@PathVariable Long itemId) {
        return menuItemService.getItem(itemId);
    }
}
