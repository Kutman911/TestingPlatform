package com.project.dao;

import com.project.db.Database;
import com.project.model.Submission;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class SubmissionDao {

    public static List<Submission> getAllSubmissions() {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT s.id, s.username, t.title AS test_name, s.score, s.status " +
                "FROM submissions s JOIN tests t ON s.test_id = t.id";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Submission sub = new Submission();
                sub.setId(rs.getInt("id"));
                sub.setStudentName(rs.getString("username"));
                sub.setTestName(rs.getString("test_name"));
                sub.setScore(rs.getDouble("score"));
                sub.setStatus(rs.getString("status"));
                submissions.add(sub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return submissions;
    }
}
