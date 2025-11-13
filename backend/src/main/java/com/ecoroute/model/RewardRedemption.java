package com.ecoroute.model;

import java.time.LocalDateTime;

public class RewardRedemption {
    private Integer redemptionId;
    private Integer userId;
    private Integer rewardId;
    private String rewardName;
    private Integer pointsCost;
    private LocalDateTime redeemedAt;

    public RewardRedemption() {
    }

    public RewardRedemption(Integer userId, Integer rewardId, String rewardName, Integer pointsCost) {
        this.userId = userId;
        this.rewardId = rewardId;
        this.rewardName = rewardName;
        this.pointsCost = pointsCost;
        this.redeemedAt = LocalDateTime.now();
    }

    public Integer getRedemptionId() {
        return redemptionId;
    }

    public void setRedemptionId(Integer redemptionId) {
        this.redemptionId = redemptionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRewardId() {
        return rewardId;
    }

    public void setRewardId(Integer rewardId) {
        this.rewardId = rewardId;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public Integer getPointsCost() {
        return pointsCost;
    }

    public void setPointsCost(Integer pointsCost) {
        this.pointsCost = pointsCost;
    }

    public LocalDateTime getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(LocalDateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }
}
