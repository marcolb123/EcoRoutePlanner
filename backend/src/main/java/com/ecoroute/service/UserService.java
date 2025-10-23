package com.ecoroute.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
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

    // Reuse an already-made guest user instead of creating a new one every time.
    public User createGuest() {
        // Prefer a canonical username "guest" if it exists
        Optional<User> byName = repo.findByUsername("guest");
        if (byName.isPresent()) {
            return byName.get();
        }

        // Fallback: return any existing user with role GUEST
        Optional<User> anyGuest = repo.findFirstByRole(User.Role.GUEST);
        if (anyGuest.isPresent()) {
            return anyGuest.get();
        }

        // No guest found: create a single canonical guest user
        User guest = new User();
        guest.setUsername("guest"); // fixed username to avoid repeated creation
        guest.setPassword(""); // no password
        guest.setRole(User.Role.GUEST);
        guest.setEcoPoints(0);

        try {
            return repo.save(guest);
        } catch (DataIntegrityViolationException ex) {
            // If another process created the guest concurrently, fetch and return it
            return repo.findByUsername("guest").orElseGet(() -> {
                // As a last resort, return the transient guest (won't be persisted)
                return guest;
            });
        }
    }
}