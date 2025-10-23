package com.ecoroute.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecoroute.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    // Return any existing user with the given role (useful to reuse a guest account)
    Optional<User> findFirstByRole(User.Role role);
}