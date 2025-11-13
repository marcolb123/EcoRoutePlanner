package com.ecoroute.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecoroute.model.User;
import com.ecoroute.service.UserService;

// NO @CrossOrigin here - use global config only
@RestController
@RequestMapping("/api/users")
public class AuthController {

    private final UserService userService;
    public AuthController(UserService userService) { 
        this.userService = userService; 
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        return userService.authenticate(username, password)
                .<ResponseEntity<?>>map(user -> {
                    user.setPassword(null);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "invalid_credentials")));
    }

    @PostMapping("/guest")
    public ResponseEntity<?> guest() {
        User u = userService.createGuest();
        u.setPassword(null);
        return ResponseEntity.ok(u);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String email = body.get("email"); // optional

        // Validate required fields
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "missing_fields"));
        }

        // Validate username format (alphanumeric, 3-20 chars)
        if (!username.matches("^[a-zA-Z0-9]{3,20}$")) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_username_format"));
        }

        // Validate password strength (min 6 chars)
        if (password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "password_too_short"));
        }

        // Validate email format if provided
        if (email != null && !email.isBlank() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid_email_format"));
        }

            // Early check to avoid database round-trip on duplicate username
            if (userService.usernameExists(username)) {
                return ResponseEntity.badRequest().body(Map.of("error", "username_taken"));
            }
    
            try {
                User created = userService.createUser(username, password, email);
                created.setPassword(null);
                return ResponseEntity.ok(created);
            } catch (RuntimeException dive) {
                // Handle database constraint/duplicate username or other runtime exceptions
                return ResponseEntity.badRequest().body(Map.of("error", "username_taken"));
                } catch (Exception ex) {
                    // Log exception in server logs for debugging
                    ex.printStackTrace();
                    return ResponseEntity.status(500).body(Map.of("error", "server_error"));
                }
        }
    
    @PutMapping("/{id}/eco-points")
    public ResponseEntity<?> updateEcoPoints(
        @PathVariable Integer id,
        @RequestBody Map<String, Integer> body) {
        
        System.out.println("Eco-points deduction request for user: " + id + ", body: " + body);
        
        Integer pointsToDeduct = body.get("pointsToDeduct");
        
        if (pointsToDeduct == null || pointsToDeduct < 0) {
            System.err.println("Invalid points amount: " + pointsToDeduct);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid points amount"));
        }
        
        return userService.deductEcoPoints(id, pointsToDeduct)
                .<ResponseEntity<?>>map(user -> {
                    user.setPassword(null); // Don't send password back
                    System.out.println("Points deducted successfully. New balance: " + user.getEcoPoints());
                    return ResponseEntity.ok(Map.of("ecoPoints", user.getEcoPoints()));
                })
                .orElseGet(() -> {
                    System.err.println("User not found or insufficient points");
                    return ResponseEntity
                            .notFound()
                            .build();
                });
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Integer id) {
        return userService.findById(id)
                .<ResponseEntity<?>>map(user -> {
                    user.setPassword(null);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(
        @PathVariable Integer id,
        @RequestBody Map<String, String> body) {
        
        return userService.updateProfile(id, body)
                .<ResponseEntity<?>>map(user -> {
                    user.setPassword(null);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(Map.of("error", "update_failed")));
    }

    @GetMapping("/{id}/purchase-history")
    public ResponseEntity<?> getPurchaseHistory(@PathVariable Integer id) {
        try {
            java.util.List<com.ecoroute.model.RewardRedemption> history = 
                com.ecoroute.database.RewardRedemptionDatabaseManager.getByUserId(id);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to fetch purchase history"));
        }
    }
}