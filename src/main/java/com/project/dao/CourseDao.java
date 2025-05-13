package com.project.dao;

import com.project.model.Course;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CourseDao {

    /**
     * Сохраняет новый курс в базе данных.
     * @param course объект Course для сохранения (courseId может быть не установлен).
     * @return courseId сохраненного курса или -1 в случае ошибки.
     * @throws SQLException если возникает ошибка доступа к базе данных.
     */
    int save(Course course) throws SQLException;

    /**
     * Обновляет существующий курс в базе данных.
     * @param course объект Course с обновленными данными (courseId должен быть установлен).
     * @return true, если обновление прошло успешно, иначе false.
     * @throws SQLException если возникает ошибка доступа к базе данных.
     */
    boolean update(Course course) throws SQLException;

    /**
     * Удаляет курс из базы данных по его ID.
     * @param courseId ID курса для удаления.
     * @return true, если удаление прошло успешно, иначе false.
     * @throws SQLException если возникает ошибка доступа к базе данных.
     */
    boolean delete(int courseId) throws SQLException;

    /**
     * Находит курс по его ID.
     * @param courseId ID курса.
     * @return Optional, содержащий объект Course, если найден, иначе пустой Optional.
     * @throws SQLException если возникает ошибка доступа к базе данных.
     */
    Optional<Course> findById(int courseId) throws SQLException;

    /**
     * Находит курс по его имени (полезно для проверки уникальности).
     * @param courseName имя курса.
     * @return Optional, содержащий объект Course, если найден, иначе пустой Optional.
     * @throws SQLException если возникает ошибка доступа к базе данных.
     */
    Optional<Course> findByName(String courseName) throws SQLException;


    /**
     * Возвращает список всех курсов.
     * @return List объектов Course.
     * @throws SQLException если возникает ошибка доступа к базе данных.
     */
    List<Course> findAll() throws SQLException;
}