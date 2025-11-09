package com.ecoroute.model;

public class Reward {
    private Integer rewardId;

    private String rewardName;

    private Integer rewardCost;

    private String description;

    private Integer stock;

    public Integer getRewardId() { return rewardId; }
    public void setRewardId(Integer rewardId) { this.rewardId = rewardId; }

    public String getRewardName() { return rewardName; }
    public void setRewardName(String rewardName) { this.rewardName = rewardName; }

    public Integer getRewardCost() { return rewardCost; }
    public void setRewardCost(Integer rewardCost) { this.rewardCost = rewardCost; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
