package com.ecoroute.model;

public class Staff extends User {
    private Integer staffId;

    private Role staffRole;

    private String employmentStatus;

    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }

    public Role getStaffRole() { return staffRole; }
    public void setStaffRole(Role staffRole) { this.staffRole = staffRole; }

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
            // copy the enum role from the User superclass
            super.setRole(user.getRole());
            setEcoPoints(user.getEcoPoints());
        }
        this.staffRole = role;
        this.employmentStatus = employmentStatus;
    }
}
