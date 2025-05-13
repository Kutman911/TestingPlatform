package com.project.dao;

import com.project.db.Database;
import com.project.model.Course;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDaoImpl implements CourseDao {

    @Override
    public int save(Course course) throws SQLException {
        String query = "INSERT INTO courses (course_name, description) VALUES (?, ?) RETURNING course_id";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, course.getCourseName());
            preparedStatement.setString(2, course.getDescription());

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error saving course: " + e.getMessage());
            if ("23505".equals(e.getSQLState())) {
                System.err.println("Course with name '" + course.getCourseName() + "' already exists.");

            }
            throw e;
        }
        return -1;
    }

    @Override
    public boolean update(Course course) throws SQLException {
        String query = "UPDATE courses SET course_name = ?, description = ?, updated_at = CURRENT_TIMESTAMP WHERE course_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, course.getCourseName());
            preparedStatement.setString(2, course.getDescription());
            preparedStatement.setInt(3, course.getCourseId());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
            if ("23505".equals(e.getSQLState())) { // Уникальность имени
                System.err.println("Another course with name '" + course.getCourseName() + "' already exists.");
            }
            throw e;
        }
    }

    @Override
    public boolean delete(int courseId) throws SQLException {
        String query = "DELETE FROM courses WHERE course_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, courseId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Course> findById(int courseId) throws SQLException {
        String query = "SELECT * FROM courses WHERE course_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, courseId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCourse(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding course by ID: " + e.getMessage());
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public Optional<Course> findByName(String courseName) throws SQLException {
        String query = "SELECT * FROM courses WHERE course_name = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, courseName);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCourse(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding course by name: " + e.getMessage());
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<Course> findAll() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM courses ORDER BY course_name ASC";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                courses.add(mapRowToCourse(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all courses: " + e.getMessage());
            throw e;
        }
        return courses;
    }

    private Course mapRowToCourse(ResultSet rs) throws SQLException {
        int id = rs.getInt("course_id");
        String name = rs.getString("course_name");
        String description = rs.getString("description");
        LocalDateTime createdAt = rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null;
        LocalDateTime updatedAt = rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null;
        return new Course(id, name, description, createdAt, updatedAt);
    }
}