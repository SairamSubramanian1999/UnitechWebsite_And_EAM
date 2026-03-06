package com.unitech.employee.controller;

import com.unitech.employee.model.User;
import com.unitech.employee.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping("/api/me")
    public Map<String, Object> me(Authentication auth) {
        if (auth == null) return Map.of();
        User u = userService.findByEmail(auth.getName());
        if (u == null) return Map.of();
        return Map.of(
                "id", u.getId(),
                "name", u.getName(),
                "email", u.getEmail(),
                "role", u.getRole().name()
        );
    }
}
