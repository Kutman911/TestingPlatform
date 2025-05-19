package com.project.dao;

import com.project.model.Submission; // Убедитесь, что импортируете модель Submission
import java.sql.SQLException;
import java.util.List;

public interface SubmissionDao {

    /**
     * Получает список всех сабмитов студентов с информацией о студенте, тесте, набранных баллах и максимальном балле.
     *
     * @return Список объектов Submission.
     * @throws SQLException Если произошла ошибка при доступе к БД.
     */
    List<Submission> getAllSubmissionsData() throws SQLException;


}