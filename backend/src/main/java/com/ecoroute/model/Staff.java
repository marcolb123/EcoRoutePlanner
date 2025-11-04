package com.ecoroute.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "staff")
public class Staff extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer staffId;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private String employmentStatus;

    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

    // No-arg constructor required by JPA
    public Staff() {
        super();
    }

    /**
     * Convenience constructor that copies fields from an existing User and sets
     * staff-specific fields (role and employmentStatus).
     */
    public Staff(User user, Role role, String employmentStatus) {
        super();
        if (user != null) {
            setId(user.getId());
            setFirstName(user.getFirstName());
            setLastName(user.getLastName());
            setStreet(user.getStreet());
            setCity(user.getCity());
            setPostCode(user.getPostCode());
            setRegisteredAt(user.getRegisteredAt());
            setSuspended(user.isSuspended());
            setUsername(user.getUsername());
            setPassword(user.getPassword());
            setEmail(user.getEmail());
            setPhoneNumber(user.getPhoneNumber());
            setRole(user.getRole());
            setEcoPoints(user.getEcoPoints());
        }
        this.role = role;
        this.employmentStatus = employmentStatus;
    }
}
