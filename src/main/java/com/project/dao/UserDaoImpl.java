package com.project.dao;

import com.project.db.Database;
import com.project.model.*;
import javafx.scene.control.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {


    @Override
    public Optional<User> findByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {

                    int id = resultSet.getInt("id");
                    String email = resultSet.getString("email");
                    String passwordHash = resultSet.getString("password");
                    String role = resultSet.getString("role");


                    return Optional.of(createUserObject(id, username, email, passwordHash, role));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while fetching user by username: " + e.getMessage());
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public boolean save(User user) throws SQLException { // Changed from void to boolean
        String query = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPasswordHash());
            // Use getRole() from the User object, which is set by subclasses
            preparedStatement.setString(4, user.getRole().toUpperCase());

            return preparedStatement.executeUpdate() > 0; // Return true if successful
        } catch (SQLException e) {
            System.err.println("Error occurred while saving the user: " + e.getMessage());
            throw e;
        }
    }

    // Retrieve user by ID
    @Override
    public Optional<User> findById(int id) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String passwordHash = resultSet.getString("password");
                    String role = resultSet.getString("role");

                    return Optional.of(createUserObject(id, username, email, passwordHash, role));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while fetching user by ID: " + e.getMessage());
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() throws SQLException {
        String query = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String passwordHash = resultSet.getString("password");
                String role = resultSet.getString("role");

                users.add(createUserObject(id, username, email, passwordHash, role));
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while fetching all users: " + e.getMessage());
            throw e;
        }
        return users;
    }

    // Update user information
    @Override
    public boolean update(User user) throws SQLException {
        String query = "UPDATE users SET username = ?, email = ?, role = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getRole());
            preparedStatement.setInt(4, user.getId());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error occurred while updating user: " + e.getMessage());
            throw e;
        }
    }

    // Update password for a specific user
    @Override
    public boolean updatePassword(int userId, String newPasswordHash) throws SQLException {
        String query = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newPasswordHash);
            preparedStatement.setInt(2, userId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error occurred while updating user password: " + e.getMessage());
            throw e;
        }
    }


    @Override
    public boolean delete(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error occurred while deleting user: " + e.getMessage());
            throw e;
        }
    }

    private User createUserObject(int id, String username, String email, String passwordHash, String role) {
        switch (role.toUpperCase()) {
            case "ADMIN":
                return new Admin(id, username, email, passwordHash);
            case "STUDENT":
                return new Student(id, username, email, passwordHash);
            case "TEACHER":
                return new Teacher(id, username, email, passwordHash);
            case "MANAGER":
                return new Manager(id, username, email, passwordHash);
            default:
                System.err.println("Unknown role encountered: " + role + ". Returning a generic `User` instance.");

                return new User(id, username, email, role, passwordHash) {
                    @Override
                    public String getDashboardTitle() {
                        return "User Dashboard";
                    }

                    @Override
                    public List<MenuItem> getRoleSpecificMenuItems() {
                        return new ArrayList<>();
                    }
                };
        }
    }
}