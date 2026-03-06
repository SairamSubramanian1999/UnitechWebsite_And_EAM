package com.unitech.employee.controller;

import com.unitech.employee.model.User;
import com.unitech.employee.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // ----------------------------------------------------
    // GET CURRENT USER PROFILE
    // ----------------------------------------------------
    @GetMapping
    public ResponseEntity<?> getProfile(Authentication auth) {
        Map<String, Object> resp = new HashMap<>();

        try {
            String email = auth.getName();
            User u = userService.findByEmail(email);

            if (u == null) {
                resp.put("error", "User not found");
                return ResponseEntity.badRequest().body(resp);
            }

            resp.put("name", u.getName());
            resp.put("email", u.getEmail());
            resp.put("role", u.getRole());
            resp.put("createdAt", u.getCreatedAt());
            return ResponseEntity.ok(resp);

        } catch (Exception ex) {
            resp.put("error", "Unable to load profile: " + ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    // ----------------------------------------------------
    // UPDATE EMAIL
    // ----------------------------------------------------
    @PutMapping("/email")
    public ResponseEntity<?> updateEmail(Authentication auth,
                                         @RequestBody Map<String, String> body) {
        Map<String, Object> resp = new HashMap<>();

        try {
            String currentEmail = auth.getName();
            String newEmail = body.get("email");

            if (newEmail == null || newEmail.trim().isEmpty()) {
                resp.put("error", "Email cannot be empty");
                return ResponseEntity.badRequest().body(resp);
            }

            userService.updateEmail(currentEmail, newEmail);

            resp.put("message", "Email updated successfully. Please log in again.");
            return ResponseEntity.ok(resp);

        } catch (Exception ex) {
            resp.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    // ----------------------------------------------------
    // CHANGE PASSWORD
    // ----------------------------------------------------
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(Authentication auth,
                                            @RequestBody Map<String, String> body) {
        Map<String, Object> resp = new HashMap<>();

        try {
            String email = auth.getName();
            String oldPass = body.get("oldPassword");
            String newPass = body.get("newPassword");

            if (oldPass == null || newPass == null ||
                oldPass.isBlank() || newPass.isBlank()) {

                resp.put("error", "Both old and new password are required");
                return ResponseEntity.badRequest().body(resp);
            }

            userService.changePassword(email, oldPass, newPass);

            resp.put("message", "Password changed successfully");
            return ResponseEntity.ok(resp);

        } catch (Exception ex) {
            resp.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }
}
