package com.foodrush.user.controller;

import com.foodrush.user.dto.UserResponse;
import com.foodrush.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Any logged-in user
    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return userService.getByEmail(authentication.getName());
    }

    // ADMIN only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> all() {
        return userService.getAll();
    }
}
