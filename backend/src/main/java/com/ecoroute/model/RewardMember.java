package com.ecoroute.model;

public class RewardMember {
    private Integer rewardMemberId;

    private Integer rewardId;

    private Integer memberId;

    // ISO-8601 timestamp (stored as text in sqlite DB managers)
    private String redeemedAt;

    public Integer getRewardMemberId() { return rewardMemberId; }
    public void setRewardMemberId(Integer rewardMemberId) { this.rewardMemberId = rewardMemberId; }

    public Integer getRewardId() { return rewardId; }
    public void setRewardId(Integer rewardId) { this.rewardId = rewardId; }

    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }

    public String getRedeemedAt() { return redeemedAt; }
    public void setRedeemedAt(String redeemedAt) { this.redeemedAt = redeemedAt; }
}
