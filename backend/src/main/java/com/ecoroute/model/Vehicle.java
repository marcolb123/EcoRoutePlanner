package com.ecoroute.model;

import java.math.BigDecimal;

public class Vehicle {
    private Integer vehicleId;
    private String brand;
    private String model;
    private Integer year;
    private String fuelType; // PETROL, DIESEL, ELECTRIC, HYBRID, PLUG_IN_HYBRID
    private BigDecimal co2EmissionsGPerKm; // grams of CO2 per kilometer
    private BigDecimal fuelConsumptionLPer100Km; // liters per 100km (null for electric)
    private BigDecimal electricRangeKm; // kilometers (for electric/hybrid)
    private BigDecimal batteryCapacityKwh; // kWh (for electric/hybrid)
    private String vehicleType; // SEDAN, SUV, HATCHBACK, TRUCK, VAN, SPORTS, COMPACT

    // Getters and setters
    public Integer getVehicleId() { return vehicleId; }
    public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public BigDecimal getCo2EmissionsGPerKm() { return co2EmissionsGPerKm; }
    public void setCo2EmissionsGPerKm(BigDecimal co2EmissionsGPerKm) { 
        this.co2EmissionsGPerKm = co2EmissionsGPerKm; 
    }

    public BigDecimal getFuelConsumptionLPer100Km() { return fuelConsumptionLPer100Km; }
    public void setFuelConsumptionLPer100Km(BigDecimal fuelConsumptionLPer100Km) { 
        this.fuelConsumptionLPer100Km = fuelConsumptionLPer100Km; 
    }

    public BigDecimal getElectricRangeKm() { return electricRangeKm; }
    public void setElectricRangeKm(BigDecimal electricRangeKm) { 
        this.electricRangeKm = electricRangeKm; 
    }

    public BigDecimal getBatteryCapacityKwh() { return batteryCapacityKwh; }
    public void setBatteryCapacityKwh(BigDecimal batteryCapacityKwh) { 
        this.batteryCapacityKwh = batteryCapacityKwh; 
    }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
}
