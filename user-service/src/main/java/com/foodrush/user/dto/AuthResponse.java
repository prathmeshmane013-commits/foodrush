package com.foodrush.user.dto;

import com.foodrush.user.entity.Role;

public record AuthResponse(
        String token,
        String tokenType,
        String name,
        String email,
        Role role
) {}
