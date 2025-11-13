package com.ecoroute.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ecoroute.model.Member;

public class MemberDatabaseManager extends DatabaseManager {

    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Member (
                MemberId INTEGER PRIMARY KEY AUTOINCREMENT,
                UserId INTEGER NOT NULL,
                Points INTEGER NOT NULL,
                CustomerType TEXT NOT NULL,
                FOREIGN KEY (UserId) REFERENCES User(UserId)
            );
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insert(Member member) {
        String sql = "INSERT INTO Member (UserId, Points, CustomerType) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, member.getId()); // Member extends User, so getId() returns the user ID
            pstmt.setInt(2, member.getPoints() == null ? 0 : member.getPoints());
            pstmt.setString(3, member.getCustomerType());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Member> getAll() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM Member";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Member m = new Member();
                m.setMemberId(rs.getInt("MemberId"));
                m.setPoints(rs.getInt("Points"));
                m.setCustomerType(rs.getString("CustomerType"));
                members.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }

    public static void update(Member member) {
        String sql = "UPDATE Member SET Points = ?, CustomerType = ? WHERE MemberId = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, member.getPoints() == null ? 0 : member.getPoints());
            pstmt.setString(2, member.getCustomerType());
            pstmt.setInt(3, member.getMemberId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(int memberId) {
        String sql = "DELETE FROM Member WHERE MemberId = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
