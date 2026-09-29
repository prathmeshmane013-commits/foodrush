package com.foodrush.restaurant.config;

import com.foodrush.restaurant.entity.MenuItem;
import com.foodrush.restaurant.entity.Restaurant;
import com.foodrush.restaurant.repository.MenuItemRepository;
import com.foodrush.restaurant.repository.RestaurantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Adds sample restaurants on first start so there is something to test with (development only). */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public DataInitializer(RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public void run(String... args) {
        if (restaurantRepository.count() > 0) {
            return;
        }

        Restaurant spice = saveRestaurant("Spice Garden", "North Indian favourites", "FC Road, Pune", "Indian");
        saveItem(spice, "Paneer Butter Masala", "Creamy tomato gravy", "220.00");
        saveItem(spice, "Butter Naan", "Soft tandoor bread", "40.00");
        saveItem(spice, "Veg Biryani", "Fragrant rice with vegetables", "180.00");

        Restaurant pizza = saveRestaurant("Pizza Palace", "Wood-fired pizzas", "Koregaon Park, Pune", "Italian");
        saveItem(pizza, "Margherita", "Classic cheese pizza", "250.00");
        saveItem(pizza, "Farmhouse", "Loaded with vegetables", "320.00");

        System.out.println(">>> Sample restaurants and menu items created");
    }

    private Restaurant saveRestaurant(String name, String description, String address, String cuisine) {
        Restaurant r = new Restaurant();
        r.setName(name);
        r.setDescription(description);
        r.setAddress(address);
        r.setCuisine(cuisine);
        return restaurantRepository.save(r);
    }

    private void saveItem(Restaurant restaurant, String name, String description, String price) {
        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(new BigDecimal(price));
        item.setAvailable(true);
        menuItemRepository.save(item);
    }
}
