package com.ecoroute.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecoroute.database.JourneyDatabaseManager;
import com.ecoroute.model.Journey;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @GetMapping("/totalJourneys")
    public ResponseEntity<Integer> getTotalJourneys(@RequestParam int userId) {
        try {
            List<Journey> journeys = JourneyDatabaseManager.getAll();
            if (journeys == null || journeys.isEmpty()) {
                return ResponseEntity.ok(0);
            }
            int totalJourneys = (int) journeys.stream()
                .filter(journey -> journey.getUserId() != null && journey.getUserId().equals(userId))
                .count();
            return ResponseEntity.ok(totalJourneys);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/totalDistance")
    public ResponseEntity<BigDecimal> getTotalDistance(@RequestParam int userId) {
        try {
            List<Journey> journeys = JourneyDatabaseManager.getAll();
            if (journeys == null || journeys.isEmpty()) {
                return ResponseEntity.ok(BigDecimal.ZERO);
            }
            List<Journey> filteredJourneys = journeys.stream()
                .filter(journey -> journey.getUserId() != null && journey.getUserId().equals(userId))
                .toList();
            BigDecimal totalDistance = BigDecimal.ZERO;
            for (Journey journey : filteredJourneys) {
                if (journey != null && journey.getDistance() != null) {
                    totalDistance = totalDistance.add(journey.getDistance());
                }
            }
            return ResponseEntity.ok(totalDistance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/averageDistance")
    public ResponseEntity<BigDecimal> getAverageDistance(@RequestParam int userId) {
        try {
            List<Journey> journeys = JourneyDatabaseManager.getAll();
            if (journeys == null || journeys.isEmpty()) {
                return ResponseEntity.ok(BigDecimal.ZERO);
            }
            List<Journey> filteredJourneys = journeys.stream()
                .filter(journey -> journey.getUserId() != null && journey.getUserId().equals(userId))
                .toList();
            
            if (filteredJourneys.isEmpty()) {
                return ResponseEntity.ok(BigDecimal.ZERO);
            }
            
            BigDecimal totalDistance = BigDecimal.ZERO;
            for (Journey journey : filteredJourneys) {
                if (journey != null && journey.getDistance() != null) {
                    totalDistance = totalDistance.add(journey.getDistance());
                }
            }
            BigDecimal averageDistance = totalDistance.divide(BigDecimal.valueOf(filteredJourneys.size()), 2, RoundingMode.HALF_UP);
            return ResponseEntity.ok(averageDistance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/totalEmissions")
    public ResponseEntity<BigDecimal> getTotalEmissions(@RequestParam int userId) {
        try {
            List<Journey> journeys = JourneyDatabaseManager.getAll();
            if (journeys == null || journeys.isEmpty()) {
                return ResponseEntity.ok(BigDecimal.ZERO);
            }
            List<Journey> filteredJourneys = journeys.stream()
                .filter(journey -> journey.getUserId() != null && journey.getUserId().equals(userId))
                .toList();
            BigDecimal totalEmissions = BigDecimal.ZERO;
            for (Journey journey : filteredJourneys) {
                if (journey != null && journey.getEmissions() != null) {
                    totalEmissions = totalEmissions.add(journey.getEmissions());
                }
            }
            return ResponseEntity.ok(totalEmissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/averageEmissions")
    public ResponseEntity<BigDecimal> getAverageEmissions(@RequestParam int userId) {
        try {
            List<Journey> journeys = JourneyDatabaseManager.getAll();
            if (journeys == null || journeys.isEmpty()) {
                return ResponseEntity.ok(BigDecimal.ZERO);
            }
            List<Journey> filteredJourneys = journeys.stream()
                .filter(journey -> journey.getUserId() != null && journey.getUserId().equals(userId))
                .toList();
            
            if (filteredJourneys.isEmpty()) {
                return ResponseEntity.ok(BigDecimal.ZERO);
            }
            
            BigDecimal totalEmissions = BigDecimal.ZERO;
            for (Journey journey : filteredJourneys) {
                if (journey != null && journey.getEmissions() != null) {
                    totalEmissions = totalEmissions.add(journey.getEmissions());
                }
            }
            BigDecimal averageEmissions = totalEmissions.divide(BigDecimal.valueOf(filteredJourneys.size()), 2, RoundingMode.HALF_UP);
            return ResponseEntity.ok(averageEmissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/totalEmissionsReduced")
    public ResponseEntity<BigDecimal> getTotalEmissionsReduced(@RequestParam int userId) {
        try {
            List<Journey> journeys = JourneyDatabaseManager.getAll();
            if (journeys == null || journeys.isEmpty()) {
                return ResponseEntity.ok(BigDecimal.ZERO);
            }
            List<Journey> filteredJourneys = journeys.stream()
                .filter(journey -> journey.getUserId() != null && journey.getUserId().equals(userId))
                .toList();
            BigDecimal totalEmissionsReduced = BigDecimal.ZERO;
            for (Journey journey : filteredJourneys) {
                if (journey != null && journey.getEmissionsReduced() != null) {
                    totalEmissionsReduced = totalEmissionsReduced.add(journey.getEmissionsReduced());
                }
            }
            return ResponseEntity.ok(totalEmissionsReduced);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/averageEmissionsReduced")
    public ResponseEntity<BigDecimal> getAverageEmissionsReduced(@RequestParam int userId) {
        try {
            List<Journey> journeys = JourneyDatabaseManager.getAll();
            if (journeys == null || journeys.isEmpty()) {
                return ResponseEntity.ok(BigDecimal.ZERO);
            }
            List<Journey> filteredJourneys = journeys.stream()
                .filter(journey -> journey.getUserId() != null && journey.getUserId().equals(userId))
                .toList();
            
            if (filteredJourneys.isEmpty()) {
                return ResponseEntity.ok(BigDecimal.ZERO);
            }
            
            BigDecimal totalEmissionsReduced = BigDecimal.ZERO;
            for (Journey journey : filteredJourneys) {
                if (journey != null && journey.getEmissionsReduced() != null) {
                    totalEmissionsReduced = totalEmissionsReduced.add(journey.getEmissionsReduced());
                }
            }
            BigDecimal averageEmissionsReduced = totalEmissionsReduced.divide(BigDecimal.valueOf(filteredJourneys.size()), 2, RoundingMode.HALF_UP);
            return ResponseEntity.ok(averageEmissionsReduced);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
