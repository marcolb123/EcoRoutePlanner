package com.ecoroute.model;

import java.time.LocalDateTime;

public class User {
    private Integer id;

    private String firstName;

    private String lastName;

    private String street;
    private String city;

    private String postCode;

    private LocalDateTime registeredAt;

    private boolean isSuspended = false;

    private String username;
    private String password; // in prod store hashed

    private String email;

    private String phoneNumber;

    // kept for backwards-compatibility with earlier implementation
    private UserRole role = UserRole.USER;

    private Integer ecoPoints = 0;

    // Lightweight enum kept to avoid breaking existing code that expects a simple role value.
    public enum UserRole { USER, GUEST, ADMIN }

    // --- Getters / Setters ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostCode() { return postCode; }
    public void setPostCode(String postCode) { this.postCode = postCode; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }

    public boolean isSuspended() { return isSuspended; }
    public void setSuspended(boolean suspended) { isSuspended = suspended; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public Integer getEcoPoints() { return ecoPoints; }
    public void setEcoPoints(Integer ecoPoints) { this.ecoPoints = ecoPoints; }
}
