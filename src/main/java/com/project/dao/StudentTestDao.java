package com.project.dao;

import com.project.model.TestResult;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.project.db.Database;

public class StudentTestDao {


    public List<TestResult> getResultsForStudent(int studentId) {
        List<TestResult> list = new ArrayList<>();
        String sql = "SELECT t.title, s.score, s.attempt_date "
                + "FROM student_test_attempts s "
                + "JOIN tests t ON s.test_id = t.id "
                + "WHERE s.student_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                int score = rs.getInt("score");
                Timestamp ts = rs.getTimestamp("attempt_date");
                LocalDateTime dateTime = (ts != null) ? ts.toLocalDateTime() : null;
                list.add(new TestResult(title, score, dateTime));
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
}