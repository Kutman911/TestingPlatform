package com.project.controller;

import com.project.dao.UserDAO;

import com.project.dao.UserDaoImpl; // Implementation
import com.project.model.Student; // Default new user type
import com.project.model.User;
import com.project.util.PasswordHashingService; // Your hashing service
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegistrationFormController {
    public TextField txtFullName;
    public TextField txtEmail;
    public PasswordField txtPassword;
    public PasswordField txtConfirmPassword;
    public AnchorPane pane;
    public Label lblPasswordError;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    private UserDAO userDao; // DAO instance

    public void initialize() {
        lblPasswordError.setVisible(false);
        txtFullName.setPromptText("Full Name or Username");
        txtEmail.setPromptText("Email Address");
        txtPassword.setPromptText("Password (min 8 chars)");
        txtConfirmPassword.setPromptText("Confirm Password");
        userDao = new UserDaoImpl(); // Initialize the DAO
    }

    public void btnSignup(ActionEvent actionEvent) {
        registerUser();
    }

    public void txtConfirmPasswordOnAction(ActionEvent actionEvent) {
        registerUser();
    }

    private void registerUser() {
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields are required.");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid email address.");
            txtEmail.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            lblPasswordError.setText("Passwords do not match!");
            lblPasswordError.setVisible(true);
            setBorderColor("red");
            txtPassword.requestFocus();
            return;
        } else {
            lblPasswordError.setVisible(false);
            setBorderColor("transparent");
        }

        if (password.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 8 characters long.");
            txtPassword.requestFocus();
            return;
        }

        try {

            if (userDao.findByUsername(fullName).isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists. Please choose another.");
                txtFullName.requestFocus();
                return;
            }


            String hashedPassword = PasswordHashingService.hashPassword(password);


            User newUser = new Student(0, fullName, email, hashedPassword);

            boolean saved = userDao.save(newUser);

            if (saved) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Success", "User registered successfully! Please login.");
                navigateToLogin();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Could not register the user. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();

            if (e.getSQLState().equals("23505")) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username or Email already exists.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Could not register user: " + e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Could not load the login form: " + e.getMessage());
        }
    }

    private void navigateToLogin() throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/project/view/LoginForm.fxml")));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) pane.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.centerOnScreen();
    }

    private void setBorderColor(String color) {
        String style = "-fx-border-color: " + color + "; -fx-border-width: 1px;";
        if ("transparent".equals(color)) {
            style = ""; // Or your default style for no border
        }
        txtPassword.setStyle(style);
        txtConfirmPassword.setStyle(style);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}