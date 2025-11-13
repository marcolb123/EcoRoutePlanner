package com.ecoroute.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ecoroute.model.Journey;

public class JourneyDatabaseManager extends DatabaseManager {

    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Journey (
                JourneyId INTEGER PRIMARY KEY AUTOINCREMENT,
                UserId INTEGER,
                Vehicle TEXT,
                Distance REAL,
                Emissions REAL,
                EmissionsReduced REAL,
                TravelDate TEXT,
                TravelingFrom TEXT,
                TravelingTo TEXT,
                FOREIGN KEY(UserId) REFERENCES User(UserId)
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            // Add UserId column if it doesn't exist (migration from MemberId)
            try {
                stmt.executeUpdate("ALTER TABLE Journey ADD COLUMN UserId INTEGER;");
            } catch (SQLException ignored) {}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insert(Journey j) {
        String sql = "INSERT INTO Journey (UserId, Vehicle, Distance, Emissions, EmissionsReduced, TravelDate, TravelingFrom, TravelingTo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, j.getUserId() == null ? 0 : j.getUserId());
            pstmt.setString(2, j.getVehicle());
            pstmt.setBigDecimal(3, j.getDistance());
            pstmt.setBigDecimal(4, j.getEmissions());
            pstmt.setBigDecimal(5, j.getEmissionsReduced());
            pstmt.setString(6, j.getTravelDate() == null ? LocalDateTime.now().toString() : j.getTravelDate().toString());
            pstmt.setString(7, j.getTravelingFrom());
            pstmt.setString(8, j.getTravelingTo());
            pstmt.executeUpdate();
            
            // Retrieve generated ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    j.setJourneyId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Journey> getAll() {
        List<Journey> list = new ArrayList<>();
        String sql = "SELECT * FROM Journey";
        try (Connection conn = getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Journey j = new Journey();
                j.setJourneyId(rs.getInt("JourneyId"));
                
                // Safely get UserId
                try {
                    int userId = rs.getInt("UserId");
                    if (!rs.wasNull()) {
                        j.setUserId(userId);
                    }
                } catch (SQLException e) {
                    // Column might not exist in old databases
                }
                
                j.setVehicle(rs.getString("Vehicle"));
                j.setDistance(rs.getBigDecimal("Distance"));
                j.setEmissions(rs.getBigDecimal("Emissions"));
                j.setEmissionsReduced(rs.getBigDecimal("EmissionsReduced"));
                
                String td = rs.getString("TravelDate");
                if (td != null && !td.isEmpty()) {
                    try {
                        j.setTravelDate(LocalDateTime.parse(td));
                    } catch (Exception ignored) {}
                }
                
                j.setTravelingFrom(rs.getString("TravelingFrom"));
                j.setTravelingTo(rs.getString("TravelingTo"));
                list.add(j);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void update(Journey j) {
        String sql = "UPDATE Journey SET UserId = ?, Vehicle = ?, Distance = ?, Emissions = ?, EmissionsReduced = ?, TravelDate = ?, TravelingFrom = ?, TravelingTo = ? WHERE JourneyId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, j.getUserId() == null ? 0 : j.getUserId());
            pstmt.setString(2, j.getVehicle());
            pstmt.setBigDecimal(3, j.getDistance());
            pstmt.setBigDecimal(4, j.getEmissions());
            pstmt.setBigDecimal(5, j.getEmissionsReduced());
            pstmt.setString(6, j.getTravelDate() == null ? null : j.getTravelDate().toString());
            pstmt.setString(7, j.getTravelingFrom());
            pstmt.setString(8, j.getTravelingTo());
            pstmt.setInt(9, j.getJourneyId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(int journeyId) {
        String sql = "DELETE FROM Journey WHERE JourneyId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, journeyId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
