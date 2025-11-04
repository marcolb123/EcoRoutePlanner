package com.ecoroute.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "journeys")
public class Journey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer journeyId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String vehicle;

    private BigDecimal distance;

    private BigDecimal emissions;

    private BigDecimal emissionsReduced;

    @Column(name = "travel_date")
    private LocalDateTime travelDate;

    private String travelingFrom;

    private String travelingTo;

    public Integer getJourneyId() { return journeyId; }
    public void setJourneyId(Integer journeyId) { this.journeyId = journeyId; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

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
