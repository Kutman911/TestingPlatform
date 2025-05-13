
package com.project.dao;

import com.project.db.Database;
import com.project.model.Question;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class QuestionDao {


    public static List<Question> getQuestionsByTest(int testId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT id, test_id, question_text, question_type FROM questions WHERE test_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, testId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Question q = new Question();
                q.setId(rs.getInt("id"));
                q.setTestId(rs.getInt("test_id"));
                q.setText(rs.getString("text"));
                q.setType(rs.getString("type"));
                questions.add(q);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }


    public static void addQuestion(Question question) {
        String sql = "INSERT INTO questions (test_id, question_text, question_type) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, question.getTestId());
            pstmt.setString(2, question.getText());
            pstmt.setString(3, question.getType());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void deleteQuestion(int id) {
        String sql = "DELETE FROM questions WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
