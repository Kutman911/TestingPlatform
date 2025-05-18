package com.project.dao;

import com.project.model.TestResult; // Возможно, позже понадобится модель TestResult
import com.project.model.StudentAnswer; // Потребуется модель для ответа студента
import com.project.model.TestAttempt; // Потребуется модель для попытки теста

import java.sql.SQLException;
import java.util.List;

public interface StudentTestDao {

    /**
     * Сохраняет новую попытку прохождения теста.
     *
     * @param attempt Объект TestAttempt с данными попытки (кроме attempt_id).
     * @return Сгенерированный attempt_id из базы данных.
     * @throws SQLException Если произошла ошибка при доступе к БД.
     */
    int saveTestAttempt(TestAttempt attempt) throws SQLException;

    /**
     * Сохраняет ответы студента для всех вопросов в рамках одной попытки.
     *
     * @param answers Список объектов StudentAnswer.
     * @throws SQLException Если произошла ошибка при доступе к БД.
     */
    void saveStudentAnswers(List<StudentAnswer> answers) throws SQLException;

    /**
     * Получает результаты тестов для определенного студента.
     * (Этот метод уже был в вашем фрагменте)
     *
     * @param studentId ID студента.
     * @return Список объектов TestResult (или другой подходящей модели).
     * @throws SQLException Если произошла ошибка при доступе к БД.
     */
    List<TestResult> getResultsForStudent(int studentId) throws SQLException; // Зависит от модели TestResult
    boolean updateTestAttempt(TestAttempt attempt) throws SQLException;

    /**
     * Возвращает средний набранный балл по всем попыткам определенного теста.
     *
     * @param testId ID теста.
     * @return Средний балл.
     * @throws SQLException Если произошла ошибка при доступе к БД.
     */
    double getAverageScoreForTest(int testId) throws SQLException;
}