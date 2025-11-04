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
import com.ecoroute.model.Member;

public class JourneyDatabaseManager extends DatabaseManager {

    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Journey (
                JourneyId INTEGER PRIMARY KEY AUTOINCREMENT,
                MemberId INTEGER,
                Vehicle TEXT,
                Distance REAL,
                Emissions REAL,
                EmissionsReduced REAL,
                TravelDate TEXT,
                TravelingFrom TEXT,
                TravelingTo TEXT,
                FOREIGN KEY(MemberId) REFERENCES Member(MemberId)
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insert(Journey j) {
        String sql = "INSERT INTO Journey (MemberId, Vehicle, Distance, Emissions, EmissionsReduced, TravelDate, TravelingFrom, TravelingTo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, j.getMember() == null ? 0 : j.getMember().getMemberId());
            pstmt.setString(2, j.getVehicle());
            pstmt.setBigDecimal(3, j.getDistance());
            pstmt.setBigDecimal(4, j.getEmissions());
            pstmt.setBigDecimal(5, j.getEmissionsReduced());
            pstmt.setString(6, j.getTravelDate() == null ? null : j.getTravelDate().toString());
            pstmt.setString(7, j.getTravelingFrom());
            pstmt.setString(8, j.getTravelingTo());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Journey> getAll() {
        List<Journey> list = new ArrayList<>();
        String sql = "SELECT * FROM Journey";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Journey j = new Journey();
                j.setJourneyId(rs.getInt("JourneyId"));
                int memberId = rs.getInt("MemberId");
                if (memberId > 0) {
                    Member m = new Member();
                    m.setMemberId(memberId);
                    j.setMember(m);
                }
                j.setVehicle(rs.getString("Vehicle"));
                j.setDistance(rs.getBigDecimal("Distance"));
                j.setEmissions(rs.getBigDecimal("Emissions"));
                j.setEmissionsReduced(rs.getBigDecimal("EmissionsReduced"));
                String td = rs.getString("TravelDate");
                if (td != null) j.setTravelDate(LocalDateTime.parse(td));
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
        String sql = "UPDATE Journey SET MemberId = ?, Vehicle = ?, Distance = ?, Emissions = ?, EmissionsReduced = ?, TravelDate = ?, TravelingFrom = ?, TravelingTo = ? WHERE JourneyId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, j.getMember() == null ? 0 : j.getMember().getMemberId());
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
