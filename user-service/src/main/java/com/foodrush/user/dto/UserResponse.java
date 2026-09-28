package com.foodrush.user.dto;

import com.foodrush.user.entity.Role;
import com.foodrush.user.entity.User;

public record UserResponse(Long id, String name, String email, Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
