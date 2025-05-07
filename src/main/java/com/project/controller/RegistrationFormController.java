package com.project.controller;

import com.project.db.Database;
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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegistrationFormController {
    public TextField txtFullName; // Changed from txtfullName for convention
    public TextField txtEmail;    // Changed from txtMobile, assuming it's for email
    public PasswordField txtPassword;
    public PasswordField txtConfirmPassword;
    public AnchorPane pane;
    public Label lblPasswordError;
    public Label lblpassword1;
    public Label lblpassword2;
    // Combined password error labels

    // Basic email validation regex
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    public void initialize() {
        lblPasswordError.setVisible(false);
        // Consider adding prompt text directly in FXML for better maintainability
        txtFullName.setPromptText("Full Name or Username");
        txtEmail.setPromptText("Email Address");
        txtPassword.setPromptText("Password");
        txtConfirmPassword.setPromptText("Confirm Password");
        lblpassword1.setVisible(false);
        lblpassword2.setVisible(false);

    }

    public void btnSignup(ActionEvent actionEvent) {
        registerUser();
    }

    // Optional: Allow registration by pressing Enter in the confirm password field
    public void txtConfirmPasswordOnAction(ActionEvent actionEvent) {
        registerUser();
    }

    private void registerUser() {
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        // Input Validation
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
            setBorderColor("transparent"); // Or your default/valid color
        }

        // Password strength (optional but recommended)
        if (password.length() < 8) { // Example: Minimum 8 characters
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 8 characters long.");
            txtPassword.requestFocus();
            return;
        }


        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users(username, email, password) VALUES (?, ?, ?)")) {
            // In a real application, HASH THE PASSWORD before storing it!
            // Example using a placeholder hashing function (replace with a strong one like BCrypt)
            // String hashedPassword = hashPassword(password);

            preparedStatement.setString(1, fullName); // Assuming fullName is used as username
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password); // Store HASHED password here

            int i = preparedStatement.executeUpdate();

            if (i > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Success", "User registered successfully! Please login.");
                navigateToLogin();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Could not register the user. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getSQLState().equals("23505")) { // Unique constraint violation (e.g., username or email exists)
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
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/project/view/LoginForm.fxml"))); // Adjust path
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) pane.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.centerOnScreen();
    }

    private void setBorderColor(String color) {
        String style = "-fx-border-color: " + color + "; -fx-border-width: 1px;"; // Added border width for visibility
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


     private String hashPassword(String password) {
        try {
             MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
             StringBuilder sb = new StringBuilder();
             for (byte b : hashedBytes) {
                 sb.append(String.format("%02x", b));
             }
             return sb.toString();
         } catch (NoSuchAlgorithmException e) {
             throw new RuntimeException("Could not hash password", e);
         }
     }
}