// File: src/main/java/com/unitech/employee/controller/AuthController.java
package com.unitech.employee.controller;

import com.unitech.employee.dto.RegistrationDto;
import com.unitech.employee.model.Role;
import com.unitech.employee.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Auth controller: forwards to static pages and handles register.
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) { this.userService = userService; }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "registered", required = false) String registered,
                            Model model) {
        return "forward:/login.html";
    }

    @GetMapping("/register")
    public String registerForm() { return "forward:/register.html"; }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegistrationDto dto) {
        try {
            if (dto.getRole() == null) dto.setRole(Role.EMPLOYEE);
            userService.register(dto);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException ex) {
            return "redirect:/register?error=" + ex.getMessage();
        } catch (Exception ex) {
            return "redirect:/register?error=Registration failed";
        }
    }
}
