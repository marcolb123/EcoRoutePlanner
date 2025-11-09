package com.ecoroute.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ecoroute.model.Staff;
import com.ecoroute.model.Role;

public class StaffDatabaseManager extends DatabaseManager {

    public static void createTable() {
        // Use consistent table/column names used across other managers (camel-case-like)
        String sql = """
            CREATE TABLE IF NOT EXISTS Staff (
                StaffId INTEGER PRIMARY KEY AUTOINCREMENT,
                RoleId INTEGER,
                EmploymentStatus TEXT,
                FOREIGN KEY(RoleId) REFERENCES Role(RoleId)
            );
        """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            // Ensure columns exist in older DBs: try to add missing columns (ignored on failure)
            try {
                stmt.executeUpdate("ALTER TABLE Staff ADD COLUMN RoleId INTEGER;");
            } catch (SQLException ignored) {}
            try {
                stmt.executeUpdate("ALTER TABLE Staff ADD COLUMN EmploymentStatus TEXT;");
            } catch (SQLException ignored) {}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insert(Staff s) {
        String sql = "INSERT INTO Staff (RoleId, EmploymentStatus) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, s.getStaffRole() == null || s.getStaffRole().getRoleId() == null ? 0 : s.getStaffRole().getRoleId());
            pstmt.setString(2, s.getEmploymentStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Staff> getAll() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM Staff";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Staff s = new Staff();
                s.setStaffId(rs.getInt("StaffId"));
                int roleId = rs.getInt("RoleId");
                if (roleId > 0) {
                    Role r = new Role();
                    r.setRoleId(roleId);
                    s.setStaffRole(r);
                }
                s.setEmploymentStatus(rs.getString("EmploymentStatus"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void update(Staff s) {
        String sql = "UPDATE Staff SET RoleId = ?, EmploymentStatus = ? WHERE StaffId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, s.getStaffRole() == null || s.getStaffRole().getRoleId() == null ? 0 : s.getStaffRole().getRoleId());
            pstmt.setString(2, s.getEmploymentStatus());
            pstmt.setInt(3, s.getStaffId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(int staffId) {
        String sql = "DELETE FROM Staff WHERE StaffId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, staffId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
