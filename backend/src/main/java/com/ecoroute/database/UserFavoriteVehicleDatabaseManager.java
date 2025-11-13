package com.ecoroute.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ecoroute.model.UserFavoriteVehicle;

public class UserFavoriteVehicleDatabaseManager extends DatabaseManager {

    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS UserFavoriteVehicle (
                FavoriteId INTEGER PRIMARY KEY AUTOINCREMENT,
                UserId INTEGER NOT NULL,
                VehicleId INTEGER NOT NULL,
                AddedAt TEXT NOT NULL,
                Nickname TEXT,
                FOREIGN KEY (UserId) REFERENCES User(UserId),
                FOREIGN KEY (VehicleId) REFERENCES Vehicle(VehicleId),
                UNIQUE(UserId, VehicleId)
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insert(UserFavoriteVehicle fav) {
        String sql = "INSERT INTO UserFavoriteVehicle (UserId, VehicleId, AddedAt, Nickname) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, fav.getUserId());
            pstmt.setInt(2, fav.getVehicleId());
            pstmt.setString(3, fav.getAddedAt() == null ? LocalDateTime.now().toString() : fav.getAddedAt().toString());
            pstmt.setString(4, fav.getNickname());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    fav.setFavoriteId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<UserFavoriteVehicle> getByUserId(int userId) {
        List<UserFavoriteVehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM UserFavoriteVehicle WHERE UserId = ? ORDER BY AddedAt DESC";
        
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToFavorite(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isFavorite(int userId, int vehicleId) {
        String sql = "SELECT COUNT(*) FROM UserFavoriteVehicle WHERE UserId = ? AND VehicleId = ?";
        
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, vehicleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void delete(int userId, int vehicleId) {
        String sql = "DELETE FROM UserFavoriteVehicle WHERE UserId = ? AND VehicleId = ?";
        
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, vehicleId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateNickname(int favoriteId, String nickname) {
        String sql = "UPDATE UserFavoriteVehicle SET Nickname = ? WHERE FavoriteId = ?";
        
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nickname);
            pstmt.setInt(2, favoriteId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static UserFavoriteVehicle mapResultSetToFavorite(ResultSet rs) throws SQLException {
        UserFavoriteVehicle fav = new UserFavoriteVehicle();
        fav.setFavoriteId(rs.getInt("FavoriteId"));
        fav.setUserId(rs.getInt("UserId"));
        fav.setVehicleId(rs.getInt("VehicleId"));
        String addedAt = rs.getString("AddedAt");
        if (addedAt != null) {
            fav.setAddedAt(LocalDateTime.parse(addedAt));
        }
        fav.setNickname(rs.getString("Nickname"));
        return fav;
    }
}
