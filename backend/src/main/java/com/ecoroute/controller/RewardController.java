package com.ecoroute.controller;

import com.ecoroute.database.RewardDatabaseManager;
import com.ecoroute.model.Reward;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rewards")

public class RewardController {

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
