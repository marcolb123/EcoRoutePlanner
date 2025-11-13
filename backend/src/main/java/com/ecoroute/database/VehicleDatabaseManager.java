package com.ecoroute.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ecoroute.model.Vehicle;

public class VehicleDatabaseManager extends DatabaseManager {

    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Vehicle (
                VehicleId INTEGER PRIMARY KEY AUTOINCREMENT,
                Brand TEXT NOT NULL,
                Model TEXT NOT NULL,
                Year INTEGER,
                FuelType TEXT NOT NULL,
                Co2EmissionsGPerKm REAL NOT NULL,
                FuelConsumptionLPer100Km REAL,
                ElectricRangeKm REAL,
                BatteryCapacityKwh REAL,
                VehicleType TEXT
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insert(Vehicle v) {
        String sql = "INSERT INTO Vehicle (Brand, Model, Year, FuelType, Co2EmissionsGPerKm, " +
                     "FuelConsumptionLPer100Km, ElectricRangeKm, BatteryCapacityKwh, VehicleType) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, v.getBrand());
            pstmt.setString(2, v.getModel());
            pstmt.setInt(3, v.getYear() == null ? 2024 : v.getYear());
            pstmt.setString(4, v.getFuelType());
            pstmt.setBigDecimal(5, v.getCo2EmissionsGPerKm());
            pstmt.setBigDecimal(6, v.getFuelConsumptionLPer100Km());
            pstmt.setBigDecimal(7, v.getElectricRangeKm());
            pstmt.setBigDecimal(8, v.getBatteryCapacityKwh());
            pstmt.setString(9, v.getVehicleType());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    v.setVehicleId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Vehicle> getAll() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM Vehicle ORDER BY Brand, Model";
        
        try (Connection conn = getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String> getAllBrands() {
        List<String> brands = new ArrayList<>();
        String sql = "SELECT DISTINCT Brand FROM Vehicle ORDER BY Brand";
        
        try (Connection conn = getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                brands.add(rs.getString("Brand"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return brands;
    }

    public static List<Vehicle> getByBrand(String brand) {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM Vehicle WHERE Brand = ? ORDER BY Model";
        
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, brand);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Vehicle getById(int vehicleId) {
        String sql = "SELECT * FROM Vehicle WHERE VehicleId = ?";
        
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehicle(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void update(Vehicle v) {
        String sql = "UPDATE Vehicle SET Brand = ?, Model = ?, Year = ?, FuelType = ?, " +
                     "Co2EmissionsGPerKm = ?, FuelConsumptionLPer100Km = ?, ElectricRangeKm = ?, " +
                     "BatteryCapacityKwh = ?, VehicleType = ? WHERE VehicleId = ?";
        
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, v.getBrand());
            pstmt.setString(2, v.getModel());
            pstmt.setInt(3, v.getYear() == null ? 2024 : v.getYear());
            pstmt.setString(4, v.getFuelType());
            pstmt.setBigDecimal(5, v.getCo2EmissionsGPerKm());
            pstmt.setBigDecimal(6, v.getFuelConsumptionLPer100Km());
            pstmt.setBigDecimal(7, v.getElectricRangeKm());
            pstmt.setBigDecimal(8, v.getBatteryCapacityKwh());
            pstmt.setString(9, v.getVehicleType());
            pstmt.setInt(10, v.getVehicleId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(int vehicleId) {
        String sql = "DELETE FROM Vehicle WHERE VehicleId = ?";
        
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setVehicleId(rs.getInt("VehicleId"));
        v.setBrand(rs.getString("Brand"));
        v.setModel(rs.getString("Model"));
        v.setYear(rs.getInt("Year"));
        v.setFuelType(rs.getString("FuelType"));
        v.setCo2EmissionsGPerKm(rs.getBigDecimal("Co2EmissionsGPerKm"));
        v.setFuelConsumptionLPer100Km(rs.getBigDecimal("FuelConsumptionLPer100Km"));
        v.setElectricRangeKm(rs.getBigDecimal("ElectricRangeKm"));
        v.setBatteryCapacityKwh(rs.getBigDecimal("BatteryCapacityKwh"));
        v.setVehicleType(rs.getString("VehicleType"));
        return v;
    }
}
