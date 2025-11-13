package com.ecoroute.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecoroute.database.UserDatabaseManager;
import com.ecoroute.model.User;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = UserDatabaseManager.getAll();
            // Don't send passwords
            users.forEach(u -> u.setPassword(null));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve users"));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        try {
            User user = UserDatabaseManager.getAll().stream()
                    .filter(u -> id.equals(u.getId()))
                    .findFirst()
                    .orElse(null);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve user"));
        }
    }

    @PutMapping("/users/{id}/suspend")
    public ResponseEntity<?> suspendUser(@PathVariable Integer id, @RequestBody Map<String, Boolean> body) {
        try {
            Boolean suspend = body.get("suspend");
            if (suspend == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing suspend parameter"));
            }

            User user = UserDatabaseManager.getAll().stream()
                    .filter(u -> id.equals(u.getId()))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            user.setSuspended(suspend);
            UserDatabaseManager.update(user);

            user.setPassword(null);
            return ResponseEntity.ok(Map.of(
                    "message", suspend ? "User suspended successfully" : "User unsuspended successfully",
                    "user", user
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user suspension status"));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            // Prevent deleting admin users or yourself (would need session management)
            User user = UserDatabaseManager.getAll().stream()
                    .filter(u -> id.equals(u.getId()))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Safety check: don't allow deleting admin users
            if (user.getRole() == User.UserRole.ADMIN) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cannot delete admin users"));
            }

            UserDatabaseManager.delete(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete user"));
        }
    }

    @GetMapping("/statistics/system")
    public ResponseEntity<?> getSystemStatistics() {
        try {
            List<User> users = UserDatabaseManager.getAll();
            
            long totalUsers = users.stream()
                    .filter(u -> u.getRole() != User.UserRole.GUEST)
                    .count();
            
            long activeUsers = users.stream()
                    .filter(u -> !u.isSuspended() && u.getRole() != User.UserRole.GUEST)
                    .count();
            
            long suspendedUsers = users.stream()
                    .filter(User::isSuspended)
                    .count();
            
            int totalEcoPoints = users.stream()
                    .mapToInt(u -> u.getEcoPoints() != null ? u.getEcoPoints() : 0)
                    .sum();

            return ResponseEntity.ok(Map.of(
                    "totalUsers", totalUsers,
                    "activeUsers", activeUsers,
                    "suspendedUsers", suspendedUsers,
                    "totalEcoPoints", totalEcoPoints
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve system statistics"));
        }
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            String roleStr = body.get("role");
            if (roleStr == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Missing role parameter"));
            }

            User.UserRole role;
            try {
                role = User.UserRole.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid role value"));
            }

            User user = UserDatabaseManager.getAll().stream()
                    .filter(u -> id.equals(u.getId()))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            user.setRole(role);
            UserDatabaseManager.update(user);

            user.setPassword(null);
            return ResponseEntity.ok(Map.of(
                    "message", "User role updated successfully",
                    "user", user
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user role"));
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User newUser) {
        try {
            // Validate required fields
            if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username is required"));
            }
            if (newUser.getEmail() == null || newUser.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email is required"));
            }
            if (newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Password is required"));
            }

            // Check if username already exists
            boolean usernameExists = UserDatabaseManager.getAll().stream()
                    .anyMatch(u -> u.getUsername().equalsIgnoreCase(newUser.getUsername()));
            if (usernameExists) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username already exists"));
            }

            // Check if email already exists
            boolean emailExists = UserDatabaseManager.getAll().stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(newUser.getEmail()));
            if (emailExists) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email already exists"));
            }

            // Set default values if not provided
            if (newUser.getRole() == null) {
                newUser.setRole(User.UserRole.USER);
            }
            if (newUser.getEcoPoints() == null) {
                newUser.setEcoPoints(0);
            }
            // isSuspended is primitive boolean, defaults to false
            newUser.setRegisteredAt(java.time.LocalDateTime.now());

            // Create the user
            UserDatabaseManager.insert(newUser);

            // Return created user without password
            newUser.setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "User created successfully",
                            "user", newUser
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create user: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User updatedUser) {
        try {
            User user = UserDatabaseManager.getAll().stream()
                    .filter(u -> id.equals(u.getId()))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Update allowed fields
            if (updatedUser.getUsername() != null && !updatedUser.getUsername().trim().isEmpty()) {
                // Check if new username conflicts with another user
                boolean usernameExists = UserDatabaseManager.getAll().stream()
                        .anyMatch(u -> !u.getId().equals(id) && u.getUsername().equalsIgnoreCase(updatedUser.getUsername()));
                if (usernameExists) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Username already exists"));
                }
                user.setUsername(updatedUser.getUsername());
            }

            if (updatedUser.getEmail() != null && !updatedUser.getEmail().trim().isEmpty()) {
                // Check if new email conflicts with another user
                boolean emailExists = UserDatabaseManager.getAll().stream()
                        .anyMatch(u -> !u.getId().equals(id) && u.getEmail().equalsIgnoreCase(updatedUser.getEmail()));
                if (emailExists) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Email already exists"));
                }
                user.setEmail(updatedUser.getEmail());
            }

            if (updatedUser.getEcoPoints() != null) {
                user.setEcoPoints(updatedUser.getEcoPoints());
            }

            // Don't allow password update through this endpoint for security
            // Role and suspended status should use their specific endpoints

            UserDatabaseManager.update(user);

            user.setPassword(null);
            return ResponseEntity.ok(Map.of(
                    "message", "User updated successfully",
                    "user", user
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user: " + e.getMessage()));
        }
    }
}
