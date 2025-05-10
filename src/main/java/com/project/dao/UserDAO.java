package com.project.dao;

import com.project.model.User; // Import the abstract User
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDAO {

    /**
     * Finds a user by their username.
     * The password check should happen in the service/controller layer after hashing the input password.
     * This method returns the user object if the username exists.
     * @param username the username to search for
     * @return an Optional containing the User object if found, otherwise empty
     * @throws SQLException if a database access error occurs
     */
    Optional<User> findByUsername(String username) throws SQLException;

    /**
     * Saves a new user to the database or updates an existing one.
     * Assumes the user object (especially password) is ready for DB (e.g., password hashed).
     *
     * @param user the User object to save (can be Student, Teacher, etc.)
     * @return
     * @throws SQLException if a database access error occurs
     */
    boolean save(User user) throws SQLException; // Could also return the saved User with ID

    /**
     * Finds a user by their ID.
     * @param id the ID of the user
     * @return an Optional containing the User if found
     * @throws SQLException if a database access error occurs
     */
    Optional<User> findById(int id) throws SQLException;

    /**
     * Retrieves all users from the database.
     * @return a list of all users
     * @throws SQLException if a database access error occurs
     */
    List<User> findAll() throws SQLException;

    /**
     * Updates an existing user's details (excluding password, handle password separately for security).
     * @param user the User object with updated details
     * @return true if update was successful
     * @throws SQLException if a database access error occurs
     */
    boolean update(User user) throws SQLException; // For general updates

    /**
     * Updates a user's password hash.
     * @param userId the ID of the user
     * @param newPasswordHash the new hashed password
     * @return true if successful
     * @throws SQLException if a database access error occurs
     */
    boolean updatePassword(int userId, String newPasswordHash) throws SQLException;


    /**
     * Deletes a user by their ID.
     * @param userId the ID of the user to delete
     * @return true if deletion was successful
     * @throws SQLException if a database access error occurs
     */
    boolean delete(int userId) throws SQLException;


}