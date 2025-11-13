package com.ecoroute.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ecoroute.model.RewardRedemption;

public class RewardRedemptionDatabaseManager extends DatabaseManager {

    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS RewardRedemption (
                RedemptionId INTEGER PRIMARY KEY AUTOINCREMENT,
                UserId INTEGER NOT NULL,
                RewardId INTEGER NOT NULL,
                RewardName TEXT NOT NULL,
                PointsCost INTEGER NOT NULL,
                RedeemedAt TEXT NOT NULL,
                FOREIGN KEY (UserId) REFERENCES User(UserId),
                FOREIGN KEY (RewardId) REFERENCES Reward(RewardId)
            );
        """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insert(RewardRedemption redemption) {
        String sql = "INSERT INTO RewardRedemption (UserId, RewardId, RewardName, PointsCost, RedeemedAt) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, redemption.getUserId());
            pstmt.setInt(2, redemption.getRewardId());
            pstmt.setString(3, redemption.getRewardName());
            pstmt.setInt(4, redemption.getPointsCost());
            pstmt.setString(5, redemption.getRedeemedAt() == null ? null : redemption.getRedeemedAt().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<RewardRedemption> getAll() {
        List<RewardRedemption> list = new ArrayList<>();
        String sql = "SELECT * FROM RewardRedemption ORDER BY RedeemedAt DESC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                RewardRedemption redemption = new RewardRedemption();
                redemption.setRedemptionId(rs.getInt("RedemptionId"));
                redemption.setUserId(rs.getInt("UserId"));
                redemption.setRewardId(rs.getInt("RewardId"));
                redemption.setRewardName(rs.getString("RewardName"));
                redemption.setPointsCost(rs.getInt("PointsCost"));
                String redeemedAt = rs.getString("RedeemedAt");
                if (redeemedAt != null) {
                    redemption.setRedeemedAt(LocalDateTime.parse(redeemedAt));
                }
                list.add(redemption);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<RewardRedemption> getByUserId(int userId) {
        List<RewardRedemption> list = new ArrayList<>();
        String sql = "SELECT * FROM RewardRedemption WHERE UserId = ? ORDER BY RedeemedAt DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                RewardRedemption redemption = new RewardRedemption();
                redemption.setRedemptionId(rs.getInt("RedemptionId"));
                redemption.setUserId(rs.getInt("UserId"));
                redemption.setRewardId(rs.getInt("RewardId"));
                redemption.setRewardName(rs.getString("RewardName"));
                redemption.setPointsCost(rs.getInt("PointsCost"));
                String redeemedAt = rs.getString("RedeemedAt");
                if (redeemedAt != null) {
                    redemption.setRedeemedAt(LocalDateTime.parse(redeemedAt));
                }
                list.add(redemption);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void delete(int redemptionId) {
        String sql = "DELETE FROM RewardRedemption WHERE RedemptionId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, redemptionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
