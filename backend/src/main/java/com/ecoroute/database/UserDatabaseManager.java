package com.ecoroute.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ecoroute.model.User;

public class UserDatabaseManager extends DatabaseManager {

    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS User (
                UserId INTEGER PRIMARY KEY AUTOINCREMENT,
                FirstName TEXT,
                LastName TEXT,
                Street TEXT,
                City TEXT,
                PostCode TEXT,
                RegisteredAt TEXT,
                IsSuspended INTEGER,
                Email TEXT,
                Password TEXT,
                PhoneNumber TEXT,
                Username TEXT UNIQUE,
                Role TEXT DEFAULT 'USER',
                EcoPoints INTEGER DEFAULT 0
            );
        """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            // Add Role and EcoPoints columns if they don't exist (for existing databases)
            try {
                stmt.executeUpdate("ALTER TABLE User ADD COLUMN Role TEXT DEFAULT 'USER';");
            } catch (SQLException ignored) {}
            try {
                stmt.executeUpdate("ALTER TABLE User ADD COLUMN EcoPoints INTEGER DEFAULT 0;");
            } catch (SQLException ignored) {}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insert(User u) {
        String sql = "INSERT INTO User (FirstName, LastName, Street, City, PostCode, RegisteredAt, IsSuspended, Email, Password, PhoneNumber, Username, Role, EcoPoints) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, u.getFirstName());
            pstmt.setString(2, u.getLastName());
            pstmt.setString(3, u.getStreet());
            pstmt.setString(4, u.getCity());
            pstmt.setString(5, u.getPostCode());
            pstmt.setString(6, u.getRegisteredAt() == null ? null : u.getRegisteredAt().toString());
            pstmt.setInt(7, u.isSuspended() ? 1 : 0);
            pstmt.setString(8, u.getEmail());
            pstmt.setString(9, u.getPassword());
            pstmt.setString(10, u.getPhoneNumber());
            pstmt.setString(11, u.getUsername());
            pstmt.setString(12, u.getRole() == null ? "USER" : u.getRole().name());
            pstmt.setInt(13, u.getEcoPoints() == null ? 0 : u.getEcoPoints());
            pstmt.executeUpdate();
            
            // Retrieve generated ID and set it on the User object
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    u.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM User";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("UserId"));
                u.setFirstName(rs.getString("FirstName"));
                u.setLastName(rs.getString("LastName"));
                u.setStreet(rs.getString("Street"));
                u.setCity(rs.getString("City"));
                u.setPostCode(rs.getString("PostCode"));
                String ra = rs.getString("RegisteredAt");
                if (ra != null) u.setRegisteredAt(LocalDateTime.parse(ra));
                u.setSuspended(rs.getInt("IsSuspended") != 0);
                u.setEmail(rs.getString("Email"));
                u.setPassword(rs.getString("Password"));
                u.setPhoneNumber(rs.getString("PhoneNumber"));
                u.setUsername(rs.getString("Username"));
                // Parse role from database
                try {
                    String roleStr = rs.getString("Role");
                    if (roleStr != null && !roleStr.isEmpty()) {
                        u.setRole(User.UserRole.valueOf(roleStr));
                    } else {
                        u.setRole(User.UserRole.USER);
                    }
                } catch (Exception e) {
                    u.setRole(User.UserRole.USER);
                }
                // Parse eco points
                try {
                    u.setEcoPoints(rs.getInt("EcoPoints"));
                } catch (Exception e) {
                    u.setEcoPoints(0);
                }
                list.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void update(User u) {
        String sql = "UPDATE User SET FirstName = ?, LastName = ?, Street = ?, City = ?, PostCode = ?, RegisteredAt = ?, IsSuspended = ?, Email = ?, Password = ?, PhoneNumber = ?, Username = ?, Role = ?, EcoPoints = ? WHERE UserId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.getFirstName());
            pstmt.setString(2, u.getLastName());
            pstmt.setString(3, u.getStreet());
            pstmt.setString(4, u.getCity());
            pstmt.setString(5, u.getPostCode());
            pstmt.setString(6, u.getRegisteredAt() == null ? null : u.getRegisteredAt().toString());
            pstmt.setInt(7, u.isSuspended() ? 1 : 0);
            pstmt.setString(8, u.getEmail());
            pstmt.setString(9, u.getPassword());
            pstmt.setString(10, u.getPhoneNumber());
            pstmt.setString(11, u.getUsername());
            pstmt.setString(12, u.getRole() == null ? "USER" : u.getRole().name());
            pstmt.setInt(13, u.getEcoPoints() == null ? 0 : u.getEcoPoints());
            pstmt.setInt(14, u.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(int userId) {
        String sql = "DELETE FROM User WHERE UserId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
