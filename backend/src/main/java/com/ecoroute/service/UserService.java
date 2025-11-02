package com.ecoroute.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ecoroute.model.User;
import com.ecoroute.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repo;
    public UserService(UserRepository repo) { this.repo = repo; }

    public Optional<User> authenticate(String username, String password) {
        return repo.findByUsername(username)
                   .filter(u -> u.getPassword() != null && u.getPassword().equals(password));
    }

    // Reuse/create guest (existing logic)
    public User createGuest() {
        Optional<User> byName = repo.findByUsername("guest");
        if (byName.isPresent()) {
            return byName.get();
        }

        Optional<User> anyGuest = repo.findFirstByRole(User.Role.GUEST);
        if (anyGuest.isPresent()) {
            return anyGuest.get();
        }

        User guest = new User();
        guest.setUsername("guest");
        guest.setPassword("");
        guest.setRole(User.Role.GUEST);
        guest.setEcoPoints(0);
        return repo.save(guest);
    }

    // Create a normal user (for registration) - now accepts email
    public User createUser(String username, String password, String email) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(password); // NOTE: hash in production
        u.setEmail(email); // may be null
        u.setRole(User.Role.USER);
        u.setEcoPoints(0);
        return repo.save(u);
    }

    // Utility: check if username exists
    public boolean usernameExists(String username) {
        return repo.findByUsername(username).isPresent();
    }

    // Deduct eco points with validation

    public Optional<User> deductEcoPoints(Integer userId, Integer pointsToDeduct) {

        // Validate input parameters
        if (userId == null || pointsToDeduct == null) {
        return Optional.empty();
        }

    Optional<User> userOpt = repo.findById(userId);
    if (userOpt.isPresent()) {
        User user = userOpt.get();

        if (user.getEcoPoints() < pointsToDeduct) {
            return Optional.empty();  // Reject - not enough points
        }

        int newPoints = user.getEcoPoints() - pointsToDeduct;
        user.setEcoPoints(newPoints); //updates user points
        repo.save(user);
        return Optional.of(user);
    }
    return Optional.empty();
    }
}