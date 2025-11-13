package com.ecoroute.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecoroute.database.JourneyDatabaseManager;
import com.ecoroute.database.UserDatabaseManager;
import com.ecoroute.model.Journey;
import com.ecoroute.model.User;
import com.ecoroute.service.UserService;

@RestController
@RequestMapping("/api/journeys")
public class JourneyController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getJourneysByUserId(@PathVariable Integer userId) {
        try {
            List<Journey> allJourneys = JourneyDatabaseManager.getAll();
            List<Journey> userJourneys = allJourneys.stream()
                .filter(j -> j.getUserId() != null && j.getUserId().equals(userId))
                .toList();
            return ResponseEntity.ok(userJourneys);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve journeys: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJourneyById(@PathVariable Integer id) {
        try {
            List<Journey> allJourneys = JourneyDatabaseManager.getAll();
            Optional<Journey> journey = allJourneys.stream()
                .filter(j -> j.getJourneyId().equals(id))
                .findFirst();
            
            if (journey.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(journey.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve journey: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createJourney(@RequestBody Map<String, Object> request) {
        try {
            // Extract fields from request
            Integer userId = (Integer) request.get("userId");
            String vehicle = (String) request.get("vehicle");
            String travelingFrom = (String) request.get("travelingFrom");
            String travelingTo = (String) request.get("travelingTo");
            
            // Handle numeric fields
            Double distanceDouble = null;
            Object distanceObj = request.get("distance");
            if (distanceObj instanceof Number) {
                distanceDouble = ((Number) distanceObj).doubleValue();
            }
            
            Double emissionsDouble = null;
            Object emissionsObj = request.get("emissions");
            if (emissionsObj instanceof Number) {
                emissionsDouble = ((Number) emissionsObj).doubleValue();
            }
            
            Double emissionsReducedDouble = null;
            Object emissionsReducedObj = request.get("emissionsReduced");
            if (emissionsReducedObj instanceof Number) {
                emissionsReducedDouble = ((Number) emissionsReducedObj).doubleValue();
            }

            // Validate user exists
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "User not found"));
            }

            // Create journey
            Journey journey = new Journey();
            journey.setUserId(userId);
            journey.setVehicle(vehicle);
            journey.setTravelingFrom(travelingFrom);
            journey.setTravelingTo(travelingTo);
            journey.setDistance(distanceDouble != null ? BigDecimal.valueOf(distanceDouble) : BigDecimal.ZERO);
            journey.setEmissions(emissionsDouble != null ? BigDecimal.valueOf(emissionsDouble) : BigDecimal.ZERO);
            journey.setEmissionsReduced(emissionsReducedDouble != null ? BigDecimal.valueOf(emissionsReducedDouble) : BigDecimal.ZERO);
            journey.setTravelDate(LocalDateTime.now());

            JourneyDatabaseManager.insert(journey);

            // Award eco points based on emissions reduced
            int pointsEarned = calculatePoints(journey.getEmissionsReduced());
            if (pointsEarned > 0) {
                User user = userOpt.get();
                int currentPoints = user.getEcoPoints() != null ? user.getEcoPoints() : 0;
                user.setEcoPoints(currentPoints + pointsEarned);
                UserDatabaseManager.update(user);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Journey saved successfully");
            response.put("journey", journey);
            response.put("pointsEarned", pointsEarned);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create journey: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJourney(@PathVariable Integer id) {
        try {
            List<Journey> allJourneys = JourneyDatabaseManager.getAll();
            boolean exists = allJourneys.stream()
                .anyMatch(j -> j.getJourneyId().equals(id));
            
            if (!exists) {
                return ResponseEntity.notFound().build();
            }
            
            JourneyDatabaseManager.delete(id);
            return ResponseEntity.ok(Map.of("message", "Journey deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete journey: " + e.getMessage()));
        }
    }

    /**
     * Calculate eco points based on emissions reduced
     * 1 point per 0.1 kg CO2 saved
     */
    private int calculatePoints(BigDecimal emissionsReduced) {
        if (emissionsReduced == null) return 0;
        return emissionsReduced.multiply(BigDecimal.valueOf(10)).intValue();
    }
}
