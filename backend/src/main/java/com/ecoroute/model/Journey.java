package com.ecoroute.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Journey {
    private Integer journeyId;
    private Integer userId;  // Changed from Member to userId
    private String vehicle;
    private BigDecimal distance;
    private BigDecimal emissions;
    private BigDecimal emissionsReduced;
    private LocalDateTime travelDate;
    private String travelingFrom;
    private String travelingTo;

    public Integer getJourneyId() { return journeyId; }
    public void setJourneyId(Integer journeyId) { this.journeyId = journeyId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getVehicle() { return vehicle; }
    public void setVehicle(String vehicle) { this.vehicle = vehicle; }

    public BigDecimal getDistance() { return distance; }
    public void setDistance(BigDecimal distance) { this.distance = distance; }

    public BigDecimal getEmissions() { return emissions; }
    public void setEmissions(BigDecimal emissions) { this.emissions = emissions; }

    public BigDecimal getEmissionsReduced() { return emissionsReduced; }
    public void setEmissionsReduced(BigDecimal emissionsReduced) { this.emissionsReduced = emissionsReduced; }

    public LocalDateTime getTravelDate() { return travelDate; }
    public void setTravelDate(LocalDateTime travelDate) { this.travelDate = travelDate; }

    public String getTravelingFrom() { return travelingFrom; }
    public void setTravelingFrom(String travelingFrom) { this.travelingFrom = travelingFrom; }

    public String getTravelingTo() { return travelingTo; }
    public void setTravelingTo(String travelingTo) { this.travelingTo = travelingTo; }
}
