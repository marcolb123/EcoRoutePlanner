package com.ecoroute.service;

import java.util.Optional;
import java.util.UUID;

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

    public User createGuest() {
        User guest = new User();
        guest.setUsername("guest-" + UUID.randomUUID().toString().substring(0,8));
        guest.setPassword(""); // no password
        guest.setRole(User.Role.GUEST);
        guest.setEcoPoints(0);
        return repo.save(guest);
    }
}
