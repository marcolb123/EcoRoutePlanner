package com.ecoroute.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "members")
public class Member extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer memberId;

    private Integer points;

    private String customerType;

    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }

    // No-arg constructor required by JPA
    public Member() {
        super();
    }

    /**
     * Convenience constructor that copies fields from an existing User and sets
     * member-specific fields. This avoids changing the User class and still
     * leverages inheritance by using the parent's setters.
     */
    public Member(User user, Integer points, String customerType) {
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
        this.points = points;
        this.customerType = customerType;
    }
}
