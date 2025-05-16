package com.project.dao;

import com.project.db.Database;
import com.project.model.Course;
import com.project.model.Test;
import com.project.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestDaoImpl implements TestDao {


    private UserDao userDao = new UserDaoImpl();
    private CourseDao courseDao = new CourseDaoImpl();


    @Override
    public int save(Test test) throws SQLException {
        String query = "INSERT INTO tests (course_id, creator_id, test_name, description, duration_minutes, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING test_id";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            if (test.getCourseId() != null) {
                ps.setInt(1, test.getCourseId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, test.getCreatorId());
            ps.setString(3, test.getTestName());
            ps.setString(4, test.getDescription());
            ps.setInt(5, test.getDurationMinutes());
            ps.setBoolean(6, test.isActive());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error saving test: " + e.getMessage());
            throw e;
        }
        return -1;
    }

    @Override
    public boolean update(Test test) throws SQLException {
        String query = "UPDATE tests SET course_id = ?, test_name = ?, description = ?, " +
                "duration_minutes = ?, is_active = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE test_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            if (test.getCourseId() != null) {
                ps.setInt(1, test.getCourseId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, test.getTestName());
            ps.setString(3, test.getDescription());
            ps.setInt(4, test.getDurationMinutes());
            ps.setBoolean(5, test.isActive());
            ps.setInt(6, test.getTestId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating test: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean delete(int testId) throws SQLException {

        String query = "DELETE FROM tests WHERE test_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, testId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting test: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Test> findAllActive() throws SQLException {
        List<Test> tests = new ArrayList<>();
        // Используем запрос с JOIN'ами, как в findAll, но с фильтром is_active
        String query = "SELECT t.*, c.course_name, u.username as creator_name " +
                "FROM tests t " +
                "LEFT JOIN courses c ON t.course_id = c.course_id " +
                "JOIN users u ON t.creator_id = u.id " +
                "WHERE t.is_active = TRUE " + // Добавляем условие
                "ORDER BY t.test_name ASC";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tests.add(mapRowToTestWithDetails(rs)); // Используем существующий маппер
            }
        } catch (SQLException e) {
            System.err.println("Error finding all active tests: " + e.getMessage());
            throw e;
        }
        return tests;
    }

    @Override
    public Optional<Test> findById(int testId) throws SQLException {
        String query = "SELECT * FROM tests WHERE test_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, testId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToTest(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding test by ID: " + e.getMessage());
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<Test> findAll() throws SQLException {
        List<Test> tests = new ArrayList<>();

        String query = "SELECT t.*, c.course_name, u.username as creator_name " +
                "FROM tests t " +
                "LEFT JOIN courses c ON t.course_id = c.course_id " +
                "JOIN users u ON t.creator_id = u.id " +
                "ORDER BY t.test_name ASC";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tests.add(mapRowToTestWithDetails(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all tests: " + e.getMessage());
            throw e;
        }
        return tests;
    }

    @Override
    public List<Test> findByCreatorId(int creatorId) throws SQLException {
        List<Test> tests = new ArrayList<>();
        String query = "SELECT t.*, c.course_name, u.username as creator_name " +
                "FROM tests t " +
                "LEFT JOIN courses c ON t.course_id = c.course_id " +
                "JOIN users u ON t.creator_id = u.id " +
                "WHERE t.creator_id = ? ORDER BY t.test_name ASC";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, creatorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tests.add(mapRowToTestWithDetails(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding tests by creator ID: " + e.getMessage());
            throw e;
        }
        return tests;
    }

    @Override
    public List<Test> findByCourseId(int courseId) throws SQLException {
        List<Test> tests = new ArrayList<>();
        String query = "SELECT t.*, c.course_name, u.username as creator_name " +
                "FROM tests t " +
                "LEFT JOIN courses c ON t.course_id = c.course_id " +
                "JOIN users u ON t.creator_id = u.id " +
                "WHERE t.course_id = ? ORDER BY t.test_name ASC";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tests.add(mapRowToTestWithDetails(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding tests by course ID: " + e.getMessage());
            throw e;
        }
        return tests;
    }

    private Test mapRowToTest(ResultSet rs) throws SQLException {
        int testId = rs.getInt("test_id");
        Integer courseId = rs.getObject("course_id") != null ? rs.getInt("course_id") : null;
        int creatorId = rs.getInt("creator_id");
        String testName = rs.getString("test_name");
        String description = rs.getString("description");
        int durationMinutes = rs.getInt("duration_minutes");
        boolean isActive = rs.getBoolean("is_active");
        LocalDateTime createdAt = rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null;
        LocalDateTime updatedAt = rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null;

        return new Test(testId, courseId, creatorId, testName, description, durationMinutes, isActive, createdAt, updatedAt);
    }

    private Test mapRowToTestWithDetails(ResultSet rs) throws SQLException {
        Test test = mapRowToTest(rs);

        test.setCourseNameDisplay(rs.getString("course_name"));
        test.setCreatorNameDisplay(rs.getString("creator_name"));
        return test;
    }
}