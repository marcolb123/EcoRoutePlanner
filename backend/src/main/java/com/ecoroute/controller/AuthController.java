package com.ecoroute.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecoroute.model.User;
import com.ecoroute.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    public AuthController(UserService userService) { this.userService = userService; }

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
    public User guest() {
        User u = userService.createGuest();
        u.setPassword(null);
        return u;
    }
}
