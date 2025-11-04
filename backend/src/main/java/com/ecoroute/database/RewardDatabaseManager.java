package com.ecoroute.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ecoroute.model.Reward;

public class RewardDatabaseManager extends DatabaseManager {

    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Reward (
                RewardId INTEGER PRIMARY KEY AUTOINCREMENT,
                RewardName TEXT NOT NULL,
                RewardCost INTEGER NOT NULL,
                Description TEXT,
                Stock INTEGER NOT NULL
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insert(Reward r) {
        String sql = "INSERT INTO Reward (RewardName, RewardCost, Description, Stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getRewardName());
            pstmt.setInt(2, r.getRewardCost() == null ? 0 : r.getRewardCost());
            pstmt.setString(3, r.getDescription());
            pstmt.setInt(4, r.getStock() == null ? 0 : r.getStock());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Reward> getAll() {
        List<Reward> list = new ArrayList<>();
        String sql = "SELECT * FROM Reward";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reward r = new Reward();
                r.setRewardId(rs.getInt("RewardId"));
                r.setRewardName(rs.getString("RewardName"));
                r.setRewardCost(rs.getInt("RewardCost"));
                r.setDescription(rs.getString("Description"));
                r.setStock(rs.getInt("Stock"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void update(Reward r) {
        String sql = "UPDATE Reward SET RewardName = ?, RewardCost = ?, Description = ?, Stock = ? WHERE RewardId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getRewardName());
            pstmt.setInt(2, r.getRewardCost() == null ? 0 : r.getRewardCost());
            pstmt.setString(3, r.getDescription());
            pstmt.setInt(4, r.getStock() == null ? 0 : r.getStock());
            pstmt.setInt(5, r.getRewardId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(int rewardId) {
        String sql = "DELETE FROM Reward WHERE RewardId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rewardId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
