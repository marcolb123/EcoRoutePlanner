package com.ecoroute.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecoroute.database.UserFavoriteVehicleDatabaseManager;
import com.ecoroute.database.VehicleDatabaseManager;
import com.ecoroute.model.UserFavoriteVehicle;
import com.ecoroute.model.Vehicle;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @GetMapping("/all")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        try {
            List<Vehicle> vehicles = VehicleDatabaseManager.getAll();
            return ResponseEntity.ok(vehicles);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/brands")
    public ResponseEntity<List<String>> getAllBrands() {
        try {
            List<String> brands = VehicleDatabaseManager.getAllBrands();
            return ResponseEntity.ok(brands);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<Vehicle>> getVehiclesByBrand(@PathVariable String brand) {
        try {
            List<Vehicle> vehicles = VehicleDatabaseManager.getByBrand(brand);
            return ResponseEntity.ok(vehicles);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Integer id) {
        try {
            Vehicle vehicle = VehicleDatabaseManager.getById(id);
            if (vehicle != null) {
                return ResponseEntity.ok(vehicle);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/user/{userId}/favorites")
    public ResponseEntity<?> getUserFavorites(@PathVariable Integer userId) {
        try {
            List<UserFavoriteVehicle> favorites = UserFavoriteVehicleDatabaseManager.getByUserId(userId);
            
            // Map to include vehicle details
            List<Map<String, Object>> result = favorites.stream().map(fav -> {
                Vehicle vehicle = VehicleDatabaseManager.getById(fav.getVehicleId());
                Map<String, Object> item = new HashMap<>();
                item.put("favorite", fav);
                item.put("vehicle", vehicle);
                return item;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/user/{userId}/favorites")
    public ResponseEntity<?> addFavorite(@PathVariable Integer userId, @RequestBody Map<String, Object> body) {
        try {
            Integer vehicleId = (Integer) body.get("vehicleId");
            String nickname = (String) body.get("nickname");
            
            if (vehicleId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "vehicleId is required"));
            }
            
            // Check if already favorited
            if (UserFavoriteVehicleDatabaseManager.isFavorite(userId, vehicleId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Vehicle already favorited"));
            }
            
            UserFavoriteVehicle fav = new UserFavoriteVehicle();
            fav.setUserId(userId);
            fav.setVehicleId(vehicleId);
            fav.setNickname(nickname);
            UserFavoriteVehicleDatabaseManager.insert(fav);
            
            return ResponseEntity.ok(Map.of("message", "Vehicle added to favorites", "favorite", fav));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to add favorite"));
        }
    }

    @DeleteMapping("/user/{userId}/favorites/{vehicleId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Integer userId, @PathVariable Integer vehicleId) {
        try {
            UserFavoriteVehicleDatabaseManager.delete(userId, vehicleId);
            return ResponseEntity.ok(Map.of("message", "Vehicle removed from favorites"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to remove favorite"));
        }
    }
}
