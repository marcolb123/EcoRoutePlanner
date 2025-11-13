package com.ecoroute.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecoroute.database.UserDatabaseManager;
import com.ecoroute.model.User;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> authenticate(String username, String password) {
        if (username == null || password == null) return Optional.empty();
        List<User> all = UserDatabaseManager.getAll();
        return all.stream()
                  .filter(u -> username.equals(u.getUsername())
                            && passwordEncoder.matches(password, u.getPassword()))
                  .findFirst();
    }

    // Reuse/create guest (existing logic using custom DB manager)
    public User createGuest() {
        List<User> all = UserDatabaseManager.getAll();
        Optional<User> byName = all.stream().filter(u -> "guest".equals(u.getUsername())).findFirst();
        if (byName.isPresent()) return byName.get();

        // Create and persist
        User guest = new User();
        guest.setUsername("guest");
        guest.setPassword("");
        guest.setRole(User.UserRole.GUEST);
        guest.setEcoPoints(0);
        UserDatabaseManager.insert(guest);

        // Reload and return persisted user (lookup by username)
        return UserDatabaseManager.getAll().stream().filter(u -> "guest".equals(u.getUsername())).findFirst().orElse(guest);
    }

    public User createUser(String username, String password, String email) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password)); // Hash password with BCrypt
        u.setEmail(email);
        u.setRole(User.UserRole.USER); // Explicitly set role
        u.setEcoPoints(0);
        u.setRegisteredAt(java.time.LocalDateTime.now()); // Set registration time
        UserDatabaseManager.insert(u);
        return UserDatabaseManager.getAll().stream().filter(x -> username.equals(x.getUsername())).findFirst().orElse(u);
    }

    public boolean usernameExists(String username) {
        return UserDatabaseManager.getAll().stream().anyMatch(u -> username.equals(u.getUsername()));
    }

    public Optional<User> deductEcoPoints(Integer userId, Integer pointsToDeduct) {
        if (userId == null || pointsToDeduct == null) return Optional.empty();
        Optional<User> userOpt = UserDatabaseManager.getAll().stream().filter(u -> userId.equals(u.getId())).findFirst();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getEcoPoints() == null || user.getEcoPoints() < pointsToDeduct) return Optional.empty();
            user.setEcoPoints(user.getEcoPoints() - pointsToDeduct);
            UserDatabaseManager.update(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<User> findById(Integer userId) {
        if (userId == null) return Optional.empty();
        return UserDatabaseManager.getAll().stream()
                .filter(u -> userId.equals(u.getId()))
                .findFirst();
    }

    public Optional<User> updateProfile(Integer userId, java.util.Map<String, String> updates) {
        if (userId == null || updates == null) return Optional.empty();
        Optional<User> userOpt = findById(userId);
        if (userOpt.isEmpty()) return Optional.empty();
        
        User user = userOpt.get();
        if (updates.containsKey("firstName")) user.setFirstName(updates.get("firstName"));
        if (updates.containsKey("lastName")) user.setLastName(updates.get("lastName"));
        if (updates.containsKey("email")) user.setEmail(updates.get("email"));
        if (updates.containsKey("phoneNumber")) user.setPhoneNumber(updates.get("phoneNumber"));
        if (updates.containsKey("street")) user.setStreet(updates.get("street"));
        if (updates.containsKey("city")) user.setCity(updates.get("city"));
        if (updates.containsKey("postCode")) user.setPostCode(updates.get("postCode"));
        
        UserDatabaseManager.update(user);
        return Optional.of(user);
    }
}