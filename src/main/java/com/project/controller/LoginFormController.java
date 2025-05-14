package com.project.controller;

import com.project.dao.UserDao; // Interface
import com.project.dao.UserDaoImpl; // Implementation
import com.project.model.User; // Abstract User model
import com.project.util.PasswordHashingService; // Your hashing service
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class LoginFormController {
    public TextField txtUserName;
    public PasswordField txtPassword;
    public AnchorPane root;

    private UserDao userDao; // DAO instance

    public void initialize() { // Called when FXML is loaded
        userDao = new UserDaoImpl(); // Initialize the DAO
    }

    public void btnSignIn(ActionEvent actionEvent) {
        String username = txtUserName.getText();
        String plainPassword = txtPassword.getText();

        if (username.isEmpty() || plainPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Username and Password cannot be empty.");
            return;
        }

        try {
            Optional<User> userOptional = userDao.findByUsername(username);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                if (PasswordHashingService.comparePasswords(user.getPasswordHash(), plainPassword)) {

                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/com/project/view/MainForm.fxml")));
                    Parent mainFormRoot = loader.load();

                    MainFormController mainFormController = loader.getController();
                    mainFormController.initializeUser(user);

                    Scene scene = new Scene(mainFormRoot);
                    Stage primaryStage = (Stage) root.getScene().getWindow();
                    primaryStage.setScene(scene);
                    primaryStage.setTitle(user.getDashboardTitle());
                    primaryStage.centerOnScreen();
                } else {
                    // Password does not match
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
                    txtPassword.clear();
                    txtPassword.requestFocus();
                }
            } else {
                // Username not found
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
                txtUserName.clear();
                txtPassword.clear();
                txtUserName.requestFocus();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Database query failed: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Could not load the main form: " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Resource Error", "Could not find MainForm.fxml. Check the path.");
        }
    }

    public void btnSignup(ActionEvent actionEvent) {
        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/project/view/RegistrationForm.fxml")));
            Scene scene = new Scene(parent);
            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("User Registration");
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Could not load the registration form: " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Resource Error", "Could not find RegistrationForm.fxml. Check the path.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}