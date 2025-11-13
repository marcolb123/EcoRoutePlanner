package com.ecoroute.model;

import java.time.LocalDateTime;

public class UserFavoriteVehicle {
    private Integer favoriteId;
    private Integer userId;
    private Integer vehicleId;
    private LocalDateTime addedAt;
    private String nickname; // Optional user-given nickname for the vehicle

    // Getters and setters
    public Integer getFavoriteId() { return favoriteId; }
    public void setFavoriteId(Integer favoriteId) { this.favoriteId = favoriteId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getVehicleId() { return vehicleId; }
    public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}
