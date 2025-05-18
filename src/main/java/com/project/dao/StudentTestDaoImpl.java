package com.project.dao;

import com.project.db.Database;
import com.project.model.StudentAnswer;
import com.project.model.TestAttempt;
import com.project.model.TestResult;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudentTestDaoImpl implements StudentTestDao {

    @Override
    public int saveTestAttempt(TestAttempt attempt) throws SQLException {
        String query = "INSERT INTO student_test_attempts (student_id, test_id, start_time, end_time, score) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING attempt_id";
        int attemptId = -1;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, attempt.getStudentId());
            ps.setInt(2, attempt.getTestId());
            ps.setTimestamp(3, Timestamp.valueOf(attempt.getStartTime()));
            if (attempt.getEndTime() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(attempt.getEndTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }
            ps.setInt(5, attempt.getScore());


            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        attemptId = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving test attempt: " + e.getMessage());
            throw e;
        }
        return attemptId;
    }

    @Override // *** Добавьте эту строку и метод ниже ***
    public double getAverageScoreForTest(int testId) throws SQLException {
        String query = "SELECT AVG(score) FROM student_test_attempts WHERE test_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, testId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting average score for test ID " + testId + ": " + e.getMessage());
            throw e;
        }
        return 0.0;
    }

    public boolean updateTestAttempt(TestAttempt attempt) throws SQLException {
        String query = "UPDATE student_test_attempts SET end_time = ?, score = ? WHERE attempt_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setTimestamp(1, Timestamp.valueOf(attempt.getEndTime()));
            ps.setInt(2, attempt.getScore());
            ps.setInt(3, attempt.getAttemptId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating test attempt: " + e.getMessage());
            throw e;
        }
    }


    @Override
    public void saveStudentAnswers(List<StudentAnswer> answers) throws SQLException {
        String query = "INSERT INTO student_answers (attempt_id, question_id, chosen_option_id, is_correct, answered_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            for (StudentAnswer answer : answers) {
                ps.setInt(1, answer.getAttemptId());
                ps.setInt(2, answer.getQuestionId());
                if (answer.getChosenOptionId() != null) {
                    ps.setInt(3, answer.getChosenOptionId());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                ps.setBoolean(4, answer.isCorrect());
                ps.setTimestamp(5, Timestamp.valueOf(answer.getAnsweredAt()));
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            System.err.println("Error saving student answers: " + e.getMessage());
            throw e;
        }
    }



    @Override
    public List<TestResult> getResultsForStudent(int studentId) throws SQLException {
        List<TestResult> results = new ArrayList<>();

        String sql = "SELECT " +
                "sta.attempt_id, " +
                "sta.student_id, " +
                "sta.test_id, " +
                "sta.attempt_date, " +
                "sta.start_time, " +
                "sta.end_time, " +
                "sta.score, " +
                "t.test_name AS test_title " +
                "FROM student_test_attempts sta " +
                "JOIN tests t ON sta.test_id = t.test_id " +
                "WHERE sta.student_id = ? " +
                "ORDER BY sta.end_time DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TestResult result = mapRowToTestResult(rs); // Используем вспомогательный метод
                results.add(result);
            }
        } catch (SQLException e) {
            System.err.println("Error getting test results for student: " + e.getMessage());
            throw e;
        }
        return results;
    }



    private TestResult mapRowToTestResult(ResultSet rs) throws SQLException {
        return new TestResult(
                rs.getInt("attempt_id"),
                rs.getInt("student_id"),
                rs.getInt("test_id"),
                rs.getString("test_title"), // Получено из JOIN
                rs.getInt("score"),

                rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null

        );
    }
}