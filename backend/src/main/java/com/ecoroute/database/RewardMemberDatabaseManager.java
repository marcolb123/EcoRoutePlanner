package com.ecoroute.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ecoroute.model.RewardMember;

public class RewardMemberDatabaseManager extends DatabaseManager {

    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS RewardMember (
                RewardMemberId INTEGER PRIMARY KEY AUTOINCREMENT,
                RewardId INTEGER NOT NULL,
                MemberId INTEGER NOT NULL,
                RedeemedAt TEXT
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insert(RewardMember rm) {
        String sql = "INSERT INTO RewardMember (RewardId, MemberId, RedeemedAt) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rm.getRewardId() == null ? 0 : rm.getRewardId());
            pstmt.setInt(2, rm.getMemberId() == null ? 0 : rm.getMemberId());
            pstmt.setString(3, rm.getRedeemedAt());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<RewardMember> getAll() {
        List<RewardMember> list = new ArrayList<>();
        String sql = "SELECT * FROM RewardMember";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                RewardMember rm = new RewardMember();
                rm.setRewardMemberId(rs.getInt("RewardMemberId"));
                rm.setRewardId(rs.getInt("RewardId"));
                rm.setMemberId(rs.getInt("MemberId"));
                rm.setRedeemedAt(rs.getString("RedeemedAt"));
                list.add(rm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<RewardMember> getByMemberId(int memberId) {
        List<RewardMember> list = new ArrayList<>();
        String sql = "SELECT * FROM RewardMember WHERE MemberId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RewardMember rm = new RewardMember();
                    rm.setRewardMemberId(rs.getInt("RewardMemberId"));
                    rm.setRewardId(rs.getInt("RewardId"));
                    rm.setMemberId(rs.getInt("MemberId"));
                    rm.setRedeemedAt(rs.getString("RedeemedAt"));
                    list.add(rm);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void update(RewardMember rm) {
        String sql = "UPDATE RewardMember SET RewardId = ?, MemberId = ?, RedeemedAt = ? WHERE RewardMemberId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rm.getRewardId() == null ? 0 : rm.getRewardId());
            pstmt.setInt(2, rm.getMemberId() == null ? 0 : rm.getMemberId());
            pstmt.setString(3, rm.getRedeemedAt());
            pstmt.setInt(4, rm.getRewardMemberId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(int rewardMemberId) {
        String sql = "DELETE FROM RewardMember WHERE RewardMemberId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rewardMemberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
