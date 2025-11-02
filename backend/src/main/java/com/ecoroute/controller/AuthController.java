package com.ecoroute.controller;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
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

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "missing_fields"));
        }

        // Early check to avoid database round-trip on duplicate username
        if (userService.usernameExists(username)) {
            return ResponseEntity.badRequest().body(Map.of("error", "username_taken"));
        }

        try {
            User created = userService.createUser(username, password, email);
            created.setPassword(null);
            return ResponseEntity.ok(created);
        } catch (DataIntegrityViolationException dive) {
            // Fallback: concurrent registration might still cause unique constraint error
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
    
    Integer pointsToDeduct = body.get("pointsToDeduct");
    
    if (pointsToDeduct == null || pointsToDeduct < 0) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid points amount"));
    }
    
    return userService.deductEcoPoints(id, pointsToDeduct)
            .<ResponseEntity<?>>map(user -> {
                user.setPassword(null); // Don't send password back
                return ResponseEntity.ok(Map.of("ecoPoints", user.getEcoPoints()));
            })
            .orElseGet(() -> ResponseEntity
                    .notFound()
                    .build());
}
}