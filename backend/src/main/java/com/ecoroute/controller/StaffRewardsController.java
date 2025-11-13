package com.ecoroute.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecoroute.database.RewardDatabaseManager;
import com.ecoroute.model.Reward;

@RestController
@RequestMapping("/api/rewards")

public class StaffRewardsController {

    @GetMapping("/all")
    public ResponseEntity<List<Reward>> getAllRewards() {
        try {
            List<Reward> rewards = RewardDatabaseManager.getAll();
            if (rewards == null || rewards.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(rewards);
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createReward(@RequestBody Reward reward) {
        try 
        {
            if (reward == null) 
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            RewardDatabaseManager.insert(reward);
            return ResponseEntity.status(HttpStatus.CREATED).body(reward.getRewardName() + " successfully created!");
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating reward: " + e.getMessage());
        }
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<Reward> getRewardById(@PathVariable int id) {
        try {
            Reward reward = RewardDatabaseManager.getAll().stream()
                .filter(r -> r.getRewardId() == id)
                .findFirst()
                .orElse(null);
            if (reward == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(reward);
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<String> updateReward(@RequestBody Reward reward) {
        try 
        {
            if (reward == null) 
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            if (reward.getRewardId() <= 0) 
            {
                return ResponseEntity.badRequest().body("Error: Invalid reward ID.");
            }
            RewardDatabaseManager.update(reward);
            return ResponseEntity.ok(reward.getRewardName() + " successfully updated!");
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating reward: " + e.getMessage());
        }
    }

    @PostMapping("/redeem")
    public ResponseEntity<?> redeemReward(@RequestBody Map<String, Integer> body) {
        try {
            Integer rewardId = body.get("rewardId");
            Integer userId = body.get("userId");
            Integer quantity = body.getOrDefault("quantity", 1);

            if (rewardId == null || rewardId <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid reward ID"));
            }

            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid user ID"));
            }

            if (quantity == null || quantity <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid quantity"));
            }

            // Find the reward
            Reward reward = RewardDatabaseManager.getAll().stream()
                .filter(r -> r.getRewardId().equals(rewardId))
                .findFirst()
                .orElse(null);

            if (reward == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Reward not found"));
            }

            // Check stock
            if (reward.getStock() == null || reward.getStock() < quantity) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Insufficient stock"));
            }

            // Reduce stock
            reward.setStock(reward.getStock() - quantity);
            RewardDatabaseManager.update(reward);

            // Log the purchase in purchase history
            com.ecoroute.model.RewardRedemption redemption = new com.ecoroute.model.RewardRedemption(
                userId,
                rewardId,
                reward.getRewardName(),
                reward.getRewardCost()
            );
            com.ecoroute.database.RewardRedemptionDatabaseManager.insert(redemption);

            return ResponseEntity.ok(Map.of(
                "message", "Reward redeemed successfully",
                "newStock", reward.getStock()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to redeem reward: " + e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRewardById(@PathVariable int id, @RequestBody Reward updatedReward) {
        try {
            // Find existing reward
            Reward reward = RewardDatabaseManager.getAll().stream()
                .filter(r -> r.getRewardId() == id)
                .findFirst()
                .orElse(null);

            if (reward == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Reward not found"));
            }

            // Update fields
            if (updatedReward.getRewardName() != null && !updatedReward.getRewardName().trim().isEmpty()) {
                reward.setRewardName(updatedReward.getRewardName());
            }
            if (updatedReward.getDescription() != null && !updatedReward.getDescription().trim().isEmpty()) {
                reward.setDescription(updatedReward.getDescription());
            }
            if (updatedReward.getRewardCost() != null) {
                reward.setRewardCost(updatedReward.getRewardCost());
            }
            if (updatedReward.getStock() != null) {
                reward.setStock(updatedReward.getStock());
            }

            RewardDatabaseManager.update(reward);
            return ResponseEntity.ok(Map.of(
                "message", "Reward updated successfully",
                "reward", reward
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error updating reward: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRewardById(@PathVariable int id) {
        try {
            if (id <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid reward ID"));
            }
            
            // Check if reward exists
            Reward reward = RewardDatabaseManager.getAll().stream()
                .filter(r -> r.getRewardId() == id)
                .findFirst()
                .orElse(null);

            if (reward == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Reward not found"));
            }

            RewardDatabaseManager.delete(id);
            return ResponseEntity.ok(Map.of("message", "Reward deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error deleting reward: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteReward(@PathVariable int id) {
        try {
            if (id <= 0) 
            {
                return ResponseEntity.badRequest().body("Error: Invalid reward ID.");
            }
            RewardDatabaseManager.delete(id);
            return ResponseEntity.ok("Reward with ID " + id + " successfully deleted!");
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting reward: " + e.getMessage());
        }
    }
}
