package com.project.dao;

import com.project.model.Test;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TestDao {



    /**
     * Сохраняет новый тест в базе данных.
     * @param test объект Test для сохранения.
     * @return ID сохраненного теста или -1 в случае ошибки.
     * @throws SQLException если возникает ошибка доступа к базе данных.
     */
    int save(Test test) throws SQLException;

    /**
     * Обновляет существующий тест.
     * @param test объект Test с обновленными данными.
     * @return true, если обновление прошло успешно.
     * @throws SQLException если возникает ошибка доступа к базе данных.
     */
    boolean update(Test test) throws SQLException;

    /**
     * Удаляет тест по его ID.
     * @param testId ID теста для удаления.
     * @return true, если удаление прошло успешно.
     * @throws SQLException если возникает ошибка доступа к базе данных.
     */
    boolean delete(int testId) throws SQLException;

    /**
     * Находит тест по его ID.
     * @param testId ID теста.
     * @return Optional, содержащий Test, если найден.
     * @throws SQLException если возникает ошибка доступа к базе данных.
     */
    Optional<Test> findById(int testId) throws SQLException;

    /**
     * Находит все тесты.
     * @return Список всех тестов.
     * @throws SQLException если возникает ошибка доступа к базе данных.
     */
    List<Test> findAll() throws SQLException;

    /**
     * Находит все тесты, созданные определенным пользователем (учителем).
     * @param creatorId ID создателя.
     * @return Список тестов.
     * @throws SQLException если возникает ошибка доступа к базе данных.
     */
    List<Test> findByCreatorId(int creatorId) throws SQLException;

    /**
     * Находит все тесты, привязанные к определенному курсу.
     * @param courseId ID курса.
     * @return Список тестов.
     * @throws SQLException если возникает ошибка доступа к базе данных.
     */
    List<Test> findByCourseId(int courseId) throws SQLException;
}