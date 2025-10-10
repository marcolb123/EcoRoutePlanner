package com.ecoroute.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // in prod store hashed

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(name = "eco_points")
    private Integer ecoPoints = 0;

    public enum Role { USER, GUEST, ADMIN }

    // getters / setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Integer getEcoPoints() { return ecoPoints; }
    public void setEcoPoints(Integer ecoPoints) { this.ecoPoints = ecoPoints; }
}
