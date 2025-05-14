
package com.project.dao;

import com.project.db.Database;
import com.project.model.Test;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TestDao {


    public static List<Test> getAllTests() {
        List<Test> tests = new ArrayList<>();
        String sql = "SELECT id, title, time_limit_minutes, is_published FROM tests";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Test test = new Test();
                test.setId(rs.getInt("id"));
                test.setTitle(rs.getString("title"));
                test.setDuration(rs.getInt("duration"));
                test.setPublished(rs.getBoolean("published"));
                tests.add(test);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tests;
    }

    // Добавляет новый тест
    public static void addTest(Test test) {
        String sql = "INSERT INTO tests (title, time_limit_minutes, is_published) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, test.getTitle());
            pstmt.setInt(2, test.getDuration());
            pstmt.setBoolean(3, test.isPublished());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void deleteTest(int id) {
        String sql = "DELETE FROM tests WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
