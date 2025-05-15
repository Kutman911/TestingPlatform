package com.project.dao;

import com.project.model.Question;
import java.sql.SQLException;
import java.util.List;

public interface QuestionDao {
    int addQuestion(Question question) throws SQLException;
    boolean updateQuestion(Question question) throws SQLException; // Если понадобится редактирование
    boolean deleteQuestion(int questionId) throws SQLException;
    List<Question> getQuestionsByTestId(int testId) throws SQLException;
    // Optional<Question> findById(int questionId) throws SQLException; // Если нужно
}