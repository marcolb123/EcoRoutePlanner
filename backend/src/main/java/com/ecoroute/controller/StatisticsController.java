package com.ecoroute.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ecoroute.database.JourneyDatabaseManager;
import com.ecoroute.model.Journey;

@RestController
@RequestMapping("/api/statistics")

public class StatisticsController {

    @GetMapping("/totalJourneys")
    public ResponseEntity<Integer> getTotalJourneys(int memberId) {
        try {
            List<Journey> journeys = JourneyDatabaseManager.getAll();
            if (journeys == null || journeys.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            int totalJourneys = (int) journeys.stream()
                .filter(journey -> journey.getMember().getMemberId() == memberId)
                .count();
            return ResponseEntity.ok(totalJourneys);
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/totalDistance")
    public ResponseEntity<BigDecimal> getTotalDistance(int memberId) {
        try {
            List<Journey> journeys = JourneyDatabaseManager.getAll();
            if (journeys == null || journeys.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<Journey> filteredJourneys = journeys.stream()
                .filter(journey -> journey.getMember().getMemberId() == memberId)
                .toList();
            BigDecimal totalDistance = BigDecimal.ZERO;
            for (Journey journey : filteredJourneys) {
                if (journey != null && journey.getDistance() != null) {
                    totalDistance = totalDistance.add(journey.getDistance());
                }
            }
            return ResponseEntity.ok(totalDistance);
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/averageDistance")
    public ResponseEntity<BigDecimal> getAverageDistance(int memberId) {
        try {
            List<Journey> journeys = JourneyDatabaseManager.getAll();
            if (journeys == null || journeys.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<Journey> filteredJourneys = journeys.stream()
                .filter(journey -> journey.getMember().getMemberId() == memberId)
                .toList();
            BigDecimal averageDistance = BigDecimal.ZERO;
            BigDecimal totalDistance = BigDecimal.ZERO;
            for (Journey journey : filteredJourneys) {
                if (journey != null && journey.getDistance() != null) {
                    totalDistance = totalDistance.add(journey.getDistance());
                }
            }
            int totalJourneys = journeys.size();
            averageDistance = totalDistance.divide(BigDecimal.valueOf(totalJourneys),2, RoundingMode.HALF_UP);
            return ResponseEntity.ok(averageDistance);
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/totalEmissions")
    public ResponseEntity<BigDecimal> getTotalEmissions(int memberId) {
        try {
            List<Journey> journeys = JourneyDatabaseManager.getAll();
            if (journeys == null || journeys.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<Journey> filteredJourneys = journeys.stream()
                .filter(journey -> journey.getMember().getMemberId() == memberId)
                .toList();
            BigDecimal totalEmissions = BigDecimal.ZERO;
            for (Journey journey : filteredJourneys) {
                if (journey != null && journey.getEmissions() != null) {
                    totalEmissions = totalEmissions.add(journey.getEmissions());
                }
            }
            return ResponseEntity.ok(totalEmissions);
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/averageEmissions")
    public ResponseEntity<BigDecimal> getAverageEmissions(int memberId) {
        try {
            List<Journey> journeys = JourneyDatabaseManager.getAll();
            if (journeys == null || journeys.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<Journey> filteredJourneys = journeys.stream()
                .filter(journey -> journey.getMember().getMemberId() == memberId)
                .toList();
            BigDecimal averageEmissions = BigDecimal.ZERO;
            BigDecimal totalEmissions = BigDecimal.ZERO;
            for (Journey journey : filteredJourneys) {
                if (journey != null && journey.getEmissions() != null) {
                    totalEmissions = totalEmissions.add(journey.getEmissions());
                }
            }
            int totalJourneys = journeys.size();
            averageEmissions = totalEmissions.divide(BigDecimal.valueOf(totalJourneys),2, RoundingMode.HALF_UP);
            return ResponseEntity.ok(averageEmissions);
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/totalEmissionsReduced")
    public ResponseEntity<BigDecimal> getTotalEmissionsReduced(int memberId) {
        try {
            List<Journey> journeys = JourneyDatabaseManager.getAll();
            if (journeys == null || journeys.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<Journey> filteredJourneys = journeys.stream()
                .filter(journey -> journey.getMember().getMemberId() == memberId)
                .toList();
            BigDecimal totalEmissionsReduced = BigDecimal.ZERO;
            for (Journey journey : filteredJourneys) {
                if (journey != null && journey.getEmissionsReduced() != null) {
                    totalEmissionsReduced = totalEmissionsReduced.add(journey.getEmissionsReduced());
                }
            }
            return ResponseEntity.ok(totalEmissionsReduced);
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/averageEmissionsReduced")
    public ResponseEntity<BigDecimal> getAverageEmissionsReduced(int memberId) {
        try {
            List<Journey> journeys = JourneyDatabaseManager.getAll();
            if (journeys == null || journeys.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<Journey> filteredJourneys = journeys.stream()
                .filter(journey -> journey.getMember().getMemberId() == memberId)
                .toList();
            BigDecimal averageEmissionsReduced = BigDecimal.ZERO;
            BigDecimal totalEmissionsReduced = BigDecimal.ZERO;
            for (Journey journey : filteredJourneys) {
                if (journey != null && journey.getEmissionsReduced() != null) {
                    totalEmissionsReduced = totalEmissionsReduced.add(journey.getEmissionsReduced());
                }
            }
            int totalJourneys = journeys.size();
            averageEmissionsReduced = totalEmissionsReduced.divide(BigDecimal.valueOf(totalJourneys),2, RoundingMode.HALF_UP);
            return ResponseEntity.ok(averageEmissionsReduced);
        } 
        catch (Exception e) 
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
