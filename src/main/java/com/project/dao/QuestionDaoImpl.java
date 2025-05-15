package com.project.dao;

import com.project.db.Database;
import com.project.model.Question;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuestionDaoImpl implements QuestionDao {

    @Override
    public int addQuestion(Question question) throws SQLException {
        String query = "INSERT INTO questions (test_id, question_text, question_type, points) VALUES (?, ?, ?, ?) RETURNING question_id";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, question.getTestId());
            ps.setString(2, question.getQuestionText());
            ps.setString(3, question.getQuestionType());
            ps.setInt(4, question.getPoints());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error adding question: " + e.getMessage());
            throw e;
        }
        return -1;
    }

    @Override
    public boolean deleteQuestion(int questionId) throws SQLException {
        // Убедитесь, что связанные answer_options удаляются каскадно, если они есть
        String query = "DELETE FROM questions WHERE question_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, questionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting question: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Question> getQuestionsByTestId(int testId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT * FROM questions WHERE test_id = ? ORDER BY created_at ASC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, testId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                questions.add(mapRowToQuestion(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching questions by testId: " + e.getMessage());
            throw e;
        }
        return questions;
    }

    @Override
    public boolean updateQuestion(Question question) throws SQLException {
        String query = "UPDATE questions SET question_text = ?, question_type = ?, points = ? WHERE question_id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, question.getQuestionText());
            ps.setString(2, question.getQuestionType());
            ps.setInt(3, question.getPoints());
            ps.setInt(4, question.getQuestionId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating question: " + e.getMessage());
            throw e;
        }
    }


    private Question mapRowToQuestion(ResultSet rs) throws SQLException {
        return new Question(
                rs.getInt("question_id"),
                rs.getInt("test_id"),
                rs.getString("question_text"),
                rs.getString("question_type"),
                rs.getInt("points"),
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
        );
    }
}