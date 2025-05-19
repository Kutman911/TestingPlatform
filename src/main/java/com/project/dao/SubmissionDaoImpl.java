package com.project.dao;

import com.project.db.Database; // Убедитесь, что импортируете класс Database
import com.project.model.Submission; // Убедитесь, что импортируете модель Submission

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDaoImpl implements SubmissionDao {

    @Override
    public List<Submission> getAllSubmissionsData() throws SQLException {
        List<Submission> submissions = new ArrayList<>();

        // SQL запрос для объединения данных из student_test_attempts, users, tests
        // и расчета максимального балла путем объединения с questions
        // Используем CTE (Common Table Expression - WITH clause) для предварительного расчета
        // общего количества баллов за каждый тест.
        String sql = "WITH TestTotalPoints AS (" +
                "    SELECT test_id, SUM(points) AS max_score " +
                "    FROM questions " +
                "    GROUP BY test_id" +
                ") " +
                "SELECT " +
                "sta.attempt_id, " +
                "u.username AS student_name, " +
                "t.test_name, " +
                "sta.score, " +
                "COALESCE(ttp.max_score, 0) AS max_score " +
                "FROM student_test_attempts sta " +
                "JOIN users u ON sta.student_id = u.user_id " +
                "JOIN tests t ON sta.test_id = t.test_id " +
                "LEFT JOIN TestTotalPoints ttp ON sta.test_id = ttp.test_id " +
                "ORDER BY sta.end_time DESC, u.username ASC";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int attemptId = rs.getInt("attempt_id");
                String studentName = rs.getString("student_name");
                String testName = rs.getString("test_name");
                int score = rs.getInt("score");
                int maxScore = rs.getInt("max_score");

                Submission submission = new Submission(attemptId, studentName, testName, score, maxScore);
                submissions.add(submission);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all submissions data: " + e.getMessage());
            throw e;
        }

        return submissions;
    }
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